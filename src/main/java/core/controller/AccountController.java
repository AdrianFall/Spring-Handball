package core.controller;

import core.entity.Account;
import core.entity.PasswordResetToken;
import core.entity.SocialProvider;
import core.entity.VerificationToken;
import core.event.OnRegistrationCompleteEvent;
import core.event.OnResendEmailEvent;
import core.event.OnResetPasswordEvent;
import core.model.form.*;
import core.service.AccountService;
import core.service.EmailService;
import core.service.SocialService;
import core.service.exception.EmailExistsException;
import core.service.exception.EmailNotSentException;
import core.service.security.util.SecurityUtil;
import oauth2.pixelpin.api.PixelPin;
import oauth2.pixelpin.connect.PixelPinConnectionFactory;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.social.github.connect.GitHubAdapter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

/**
 * Created by Adrian on 09/05/2015.
 */
@Controller

public class AccountController {

    @Autowired
    AccountService accountService;
    @Autowired
    MessageSource messages;
    @Autowired
    EmailService emailService;
    @Autowired
    SocialService socialService;

    @Autowired
    ProviderSignInUtils providerSignInUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    public static final String MODEL_REG_FORM = "registrationForm";

    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public ModelAndView registrationConfirm(@RequestParam("token") String token, HttpServletRequest request, Model model){
        Locale locale = request.getLocale();
        ModelAndView mav = new ModelAndView();
        VerificationToken verificationToken = accountService.findVerificationToken(token);
        if (verificationToken == null) {
            String message = messages.getMessage("registration.invalidToken", null, locale);
            mav.addObject("error", message);
            mav.setViewName("redirect:/login");

            return mav;
        }

        Account acc = verificationToken.getAcc();
        Calendar cal = Calendar.getInstance();
        Long timeDiff = verificationToken.getExpiryDate().getTime() - cal.getTime().getTime();
        if (timeDiff <= 0) {
            mav.addObject("error", messages.getMessage("registration.tokenExpired", null, locale));
            mav.setViewName("redirect:/login");
            return mav;
        }

        // Activate the acc
        acc.setEnabled(true);
        Account updatedAcc = accountService.updateAccount(acc);
        if (updatedAcc == null) {

            mav.addObject("error", messages.getMessage("registration.couldNotBeActivated", null, locale));
            mav.setViewName("redirect:/login");
            return mav;
        }
        mav.addObject("msg", messages.getMessage("registration.activated", null, locale));
        mav.setViewName("redirect:/login");
        return mav;
    }

    @RequestMapping(value = "/resendEmail", method = RequestMethod.GET)
    public String resendEmail(@Valid @ModelAttribute ResendEmailForm resentEmailForm) {
        return "resendEmail";
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
    public ModelAndView getResetPassword(@RequestParam("token") String token, HttpServletRequest request,
                                   @ModelAttribute ResetPasswordForm resetPasswordForm) {
        ModelAndView mav = new ModelAndView();

        PasswordResetToken resetToken = accountService.findPasswordResetToken(token);

        if (resetToken == null) {
            mav.addObject("error", messages.getMessage("reset.password.token.invalid", null, request.getLocale()));
            mav.setViewName("redirect:/login");

        } else {
            Account acc = resetToken.getAcc();
            Calendar cal = Calendar.getInstance();
            Long timeDiff = resetToken.getExpiryDate().getTime() - cal.getTime().getTime();
            System.out.println("Time diff : " + timeDiff);
            if (timeDiff <= 0) {
                mav.addObject("error", messages.getMessage("reset.password.token.expired", null, request.getLocale()));
                mav.setViewName("redirect:/login");

            } else {

                // Proceed with reseting the password
                Authentication auth = new UsernamePasswordAuthenticationToken(acc, null, userDetailsService.loadUserByUsername(acc.getEmail()).getAuthorities());
                System.out.println("Authorities of " + acc.getEmail() + " are : " + auth.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
                mav.setViewName("resetPassword");
            }
        }

        return mav;
    }

    @RequestMapping(value = "/requestResetPassword", method = RequestMethod.GET)
    public String resetPassword(@Valid @ModelAttribute RequestResetPasswordForm requestResetPasswordForm) { return "requestResetPassword"; }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@Valid @ModelAttribute LoginForm loginForm,
                              BindingResult bResult,
                              HttpServletRequest request,
                              HttpServletResponse response,
                              @RequestParam(value = "errorAuthentication", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout) {
        ModelAndView model = new ModelAndView();
        if (error != null) {

            if (error.equals("badCredentials")) {
                bResult.rejectValue("email", "login.badCredentials");
            }
            else if (error.equals("credentialsExpired"))
                bResult.rejectValue("password", "login.credentialsExpired");

            else if (error.equals("accountExpired"))
                bResult.rejectValue("email", "login.accountExpired");
            else if (error.equals("accountLocked"))
                bResult.rejectValue("email", "login.accountLocked");
            else if (error.equals("accountDisabled")) {
                bResult.rejectValue("email", "login.accountDisabled");
                request.setAttribute("requestResendEmail", "true");
            }

            // And now cancel the parameter from the request URL
        }

        model.setViewName("login");

        if (logout != null) {
            model.addObject("msg", "Logging out out.");
            model.setViewName("logout");
        }


        return model;
    }

    @RequestMapping(value = "/requestResetPassword", method = RequestMethod.POST)
    public ModelAndView postRequestResetPassword(@Valid @ModelAttribute RequestResetPasswordForm requestResetPasswordForm,
                                          BindingResult bResult, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        System.out.println("Reseting password P00OST");

        // Check if the account exists
        Account acc = accountService.findAccount(requestResetPasswordForm.getEmail());
        if (acc == null)
            bResult.rejectValue("email", "request.reset.password.doesNotExist");
        else {
            String appUrl = request.getRequestURL().toString().split("/requestResetPassword")[0];

            emailService.sendResetPasswordEmail(new OnResetPasswordEvent(acc, request.getLocale(), appUrl));
            mav.addObject("msg", messages.getMessage("request.reset.password.success", null, request.getLocale()));
            mav.setViewName("redirect:/login");
        }

        return mav;
    }

    @RequestMapping(value = "/resendEmail", method = RequestMethod.POST)
    public ModelAndView postResendEmail(@Valid @ModelAttribute ResendEmailForm resendEmailForm,
                                        BindingResult bResult, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        Account acc = accountService.findAccount(resendEmailForm.getEmail());
        if (acc == null)
            bResult.rejectValue("email", "resend.email.doesNotExist");
        else {
            if (acc.isEnabled())
                bResult.rejectValue("email", "resend.email.alreadyActivated");
            else {
                // Attempt to resend the activation email
                String appUrl = request.getRequestURL().toString().split("/resendEmail")[0];

                emailService.resendConfirmationEmail(new OnResendEmailEvent(acc, request.getLocale(), appUrl));
                mav.addObject("msg", messages.getMessage("resend.email.success", null, request.getLocale()));
                mav.setViewName("redirect:/login");
            }
        }
        return mav;
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ModelAndView postResetPassword(@Valid @ModelAttribute ResetPasswordForm resetPasswordForm,
                                          BindingResult bResult, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();

        // Validate the form password & passConfirm match
        if (!resetPasswordForm.getPassword().equals(resetPasswordForm.getConfirmPassword())) {
            bResult.rejectValue("password", "registration.passwordMismatch");
            bResult.rejectValue("confirmPassword", "registration.passwordMismatch");
            //System.out.println("Password match validation - failed.");
        }

        if (bResult.hasErrors()) {
            // Clean the password fields before returning the form again
            resetPasswordForm.setPassword("");
            resetPasswordForm.setConfirmPassword("");

            mav.setViewName("resetPassword");
            return mav;
        }

        // proceed with reseting pass
        System.out.println("Valid reset password");
        Account acc = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Authenticated user email : " + acc.getEmail());
        // encode pass
        acc.setPassword(accountService.encodePassword(resetPasswordForm.getPassword()));
        if (accountService.updateAccount(acc) != null) {
            mav.addObject("msg", messages.getMessage("reset.password.success", null, request.getLocale()));
            mav.setViewName("redirect:/login");
        } else {
            // Clean the password fields before returning the form again
            resetPasswordForm.setPassword("");
            resetPasswordForm.setConfirmPassword("");
            bResult.rejectValue("password", "reset.password.error");
            mav.setViewName("resetPassword");
        }

        return mav;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegistrationForm(Model m, WebRequest request) {
        System.out.println("Getting form account.");
        RegistrationForm regForm = new RegistrationForm();
        // Check for provider connections
        /*Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
        if (connection != null) {
            System.out.println("Got connection with user profile: " + connection.fetchUserProfile().toString());
            System.out.println("User email is : " + connection.fetchUserProfile().getEmail());
            System.out.println("User name: " + connection.fetchUserProfile().getFirstName() + " lastname: " + connection.fetchUserProfile().getLastName());
            UserProfile userProfile = connection.fetchUserProfile();
            regForm.setEmail(userProfile.getEmail());
            regForm.setSignInProvider(SocialMediaEnum.valueOf(connection.getKey().getProviderId().toUpperCase()));
            System.out.println("Provider = " + regForm.getSignInProvider());
        } else {
            System.out.println("No provider.");
        }*/

        /*tempRegForm.setEmail("def@au.lt");*/
        m.addAttribute("registrationForm", regForm);
        return "registrationForm";
    }

    /*@RequestMapping(value = "/signin", method = RequestMethod.GET)
    public String socialSignIn(WebRequest webRequest){
        System.out.println("Social sign in .GET");
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(webRequest);
        System.out.println((connection != null) ? "connection exists " : " connection doesn't exist");
        return ("/auth/google");
    }*/

   /* @RequestMapping(value = "/auth/pixelpin", method = RequestMethod.GET)
    public ModelAndView authenticatePixelPin() {
        ModelAndView mav = new ModelAndView();

        System.out.println("Authenticate pixel pin");


        PixelPinConnectionFactory connectionFactory =
                new PixelPinConnectionFactory("clientId", "clientSecret");
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri("http://template-auth.duckdns.org/auth/pixelpin2");
        String authorizeUrl = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, params);
        response.sendRedirect(authorizeUrl);
        mav.setViewName("redirect:https");




        return mav;
    }

    @RequestMapping(value = "/auth/pixelpin2", method = RequestMethod.GET)
    public ModelAndView authenticatePixelPin2() {
        ModelAndView mav = new ModelAndView();

        System.out.println("Authenticate pixel pin 2");
        // upon receiving the callback from the provider:
        AccessGrant accessGrant = oauthOperations.exchangeForAccess(authorizationCode, "https://my-callback-url", null);
        Connection<PixelPin> connection = connectionFactory.createConnection(accessGrant);
        return mav;
    }*/

    @RequestMapping(value = "/test/pixelpin", method = RequestMethod.GET)
    public ModelAndView testPixelPin(@RequestParam(value = "code", required = false) String code,
                                             @RequestParam(value = "error", required = false) String error,
                                             @RequestParam(value = "error_description", required = false) String error_description,
                                             @RequestParam(value = "error_uri", required = false) String error_uri,
                                             HttpServletRequest request,
                                             WebRequest webRequest,
                                             HttpServletResponse response) {
        System.out.println("/auth/pixelpin");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/login");

        if (error != null) {
            mav.addObject("error", error);
            return mav;
        }

        //If a code is returned, this implies that authentication was successful
         //This code is NOT the token you need to access the user and will only be valid for a short time in order to complete the handshake.
        if (code != null && !code.equals("")) {
            System.out.println("Code exists");
            // your site should then setup a POST request to - https://ws3.pixelpin.co.uk/index.php/api/token
            String post_uri = "https://ws3.pixelpin.co.uk/index.php/api/token";
            /* with the following form-encoded values:
                client_id - the 30 digit alphanumeric code that you obtained when registering the developer account
                client_secret - the 30 digit code that you also obtained when registering the developer account
                redirect_uri - the registered redirect uri (this is not used for redirection at this point)
                code - the code you received from the original browser redirect
                grant_type=authorization_code - part of the OAuth2 spec and the only type PixelPin supports
           */

            JSONObject jsonObject = new JSONObject();
            RestTemplate rt = new RestTemplate();
            rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            rt.getMessageConverters().add(new StringHttpMessageConverter());

            String client_id = "G7U6MS2CAS028UV5VCLNHFP1VQNIGN";
            String client_secret = "9JX+LO~n?m@1it4fxq3b^6XvNYx28H";
            String redirect_uri = "http://template-auth.duckdns.org/auth/pixelpin";
            String grant_type = "authorization_code";
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.set("client_id", client_id);
            parameters.set("client_secret", client_secret);
            parameters.set("redirect_uri", redirect_uri);
            parameters.set("code", code);
            parameters.set("grant_type", grant_type);
            jsonObject = rt.postForObject(post_uri, parameters, JSONObject.class);
            System.out.println(jsonObject.toString());

            /*If this call is successful, you will receive a JSON encoded string with access_token and expires_in.
              If it fails, you will receive an HTTP error code and description.*/
            String access_token = (String) jsonObject.get("access_token");
            System.out.println("Obtained access_token = " + access_token);
            if (access_token != null) {
                /*If the previous call succeeds, OAuth2 says you would contact the resource server for user data. In the case of PixelPin,
                  this is the same web service so you should make another GET or POST request to - https://ws3.pixelpin.co.uk/index.php/api/userdata*/
                post_uri = "https://ws3.pixelpin.co.uk/index.php/api/userdata";
                /*and pass the following form-encoded or url-encoded values:
                  oauth_token - the token you received from the previous web service call*/
                parameters = new LinkedMultiValueMap<>();
                parameters.set("oauth_token", access_token);

                JSONObject userDataJSON = new JSONObject();

                userDataJSON = rt.postForObject(post_uri, parameters, JSONObject.class);

                System.out.println("userdataJSON : " + userDataJSON.toString());

                String userEmail = (String) userDataJSON.get("email");
                if (userEmail != null) {
                    // Check whether registration is needed or just login
                    String social_provider_name = "PIXELPIN";

                    // Check whether the account with userEmail already exists
                    Account existingAccount = accountService.findAccount(userEmail);
                    if (existingAccount != null) {

                        for (SocialProvider socialProvider : existingAccount.getAccSocialProviders()) {
                            if (socialProvider.getProvider_name().equals(social_provider_name)) {
                                providerSignInUtils.doPostSignUp(existingAccount.getEmail(), webRequest);
                                SecurityUtil.logInUser(existingAccount);
                                mav.setViewName("redirect:/main");
                                return mav;
                            }
                        }

                        Set<SocialProvider> accSocialProviders = existingAccount.getAccSocialProviders();
                        SocialProvider newSocialProvider = socialService.getSocialProviderByName(social_provider_name);
                        if (newSocialProvider != null)
                            accSocialProviders.add(newSocialProvider);
                        else {
                            mav.addObject("error", messages.getMessage("social.registration.provider.not.supported", null, webRequest.getLocale()));
                            return mav;
                        }
                        existingAccount.setAccSocialProviders(accSocialProviders);
                        // set the account as enabled since this (social reg) is somewhat equivalent to acc confirmation via email
                        existingAccount.setEnabled(true);
                        Account updatedAccount = accountService.updateAccount(existingAccount);
                        if (updatedAccount == null)
                            mav.addObject("error", messages.getMessage("social.registration.could.not.update.account", null, webRequest.getLocale()));
                        else {
                            SecurityUtil.logInUser(updatedAccount);
                            providerSignInUtils.doPostSignUp(updatedAccount.getEmail(), webRequest);
                            mav.setViewName("redirect:/main");
                        }

                        return mav;
                    } else { // There exists no account yet for the userProfileEmail
                        // Create new acc
                        Account newAcc = new Account();

                        newAcc.setEmail(userEmail);
                        newAcc.setPassword(UUID.randomUUID().toString());
                        Set<SocialProvider> socialProviders = new HashSet<SocialProvider>();
                        socialProviders.add(new SocialProvider(social_provider_name));
                        newAcc.setAccSocialProviders(socialProviders);
                        newAcc.setEnabled(true);

                        try {
                            Account createdAcc = accountService.createAccount(newAcc);
                            if (createdAcc != null) {
                                // Acc created
                                SecurityUtil.logInUser(createdAcc);
                                providerSignInUtils.doPostSignUp(createdAcc.getEmail(), webRequest);
                                mav.setViewName("redirect:/main");
                                return mav;
                            }
                        } catch (EmailExistsException eee) {
                            mav.addObject("error", messages.getMessage("social.registration.idExists", null, webRequest.getLocale()));
                            mav.setViewName("redirect:/login");
                        }
                    }

                } else {
                    mav.addObject("error", messages.getMessage("social.registration.email.not.provided", null, webRequest.getLocale()));
                    return mav;
                }

            } else {
                // TODO obtain the HTTP error code & desription
                mav.addObject("error", "pixelpin auth error");
            }

        }
        return mav;
    }

    @RequestMapping(value = "/social/register", method = RequestMethod.GET)
    public ModelAndView createSocialAccount(WebRequest webRequest, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/login");
        System.out.println("Social Register");
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(webRequest);
        if (connection != null) {

            UserProfile userProfile = connection.fetchUserProfile();
            System.out.println("Obtained user profile.");

            System.out.println("Connection details - api: " + connection.getApi().toString() + " profile url: " + connection.getProfileUrl() + " display name: " + connection.getDisplayName());
            /*if (connection.getApi() instanceof GitHub) {
                System.out.println("INSTANCE OF GITHUB");
                GitHub api = (GitHub) connection.getApi();

                GitHubAdapter gitHubAdapter = new GitHubAdapter();
                userProfile = gitHubAdapter.fetchUserProfile(api);
                if (userProfile == null)
                    System.out.println("user profile is null");
                System.out.println("email = " + userProfile.getEmail() + " username: " + userProfile.getUsername() + " name: " + userProfile.getName());
            }*/


            String userProfileEmail = userProfile.getEmail();
            String socialProviderName = connection.getKey().getProviderId().toUpperCase();

            if (userProfileEmail == null) {
                mav.addObject("error", messages.getMessage("social.registration.email.not.provided", null, webRequest.getLocale()));
                return mav;
            }

            // Check whether the account with userProfileEmail already exists
            Account existsingAccount = accountService.findAccount(userProfileEmail);
            if (existsingAccount != null) { // if the accounts exists
                Set<SocialProvider> accSocialProviders = existsingAccount.getAccSocialProviders();
                SocialProvider newSocialProvider = socialService.getSocialProviderByName(socialProviderName);
                if (newSocialProvider != null)
                    accSocialProviders.add(newSocialProvider);
                else {
                    mav.addObject("error", messages.getMessage("social.registration.provider.not.supported", null, webRequest.getLocale()));
                    return mav;
                }
                existsingAccount.setAccSocialProviders(accSocialProviders);
                // set the account as enabled since this (social reg) is somewhat equivalent to acc confirmation via email
                existsingAccount.setEnabled(true);
                Account updatedAccount = accountService.updateAccount(existsingAccount);
                if (updatedAccount == null)
                    mav.addObject("error", messages.getMessage("social.registration.could.not.update.account", null, webRequest.getLocale()));
                else {
                    SecurityUtil.logInUser(updatedAccount);
                    providerSignInUtils.doPostSignUp(updatedAccount.getEmail(), webRequest);
                    mav.setViewName("redirect:/main");
                }

                return mav;
            } else { // There exists no account yet for the userProfileEmail
                // Create new acc
                Account newAcc = new Account();

                newAcc.setEmail(userProfileEmail);
                newAcc.setPassword(UUID.randomUUID().toString());
                Set<SocialProvider> socialProviders = new HashSet<SocialProvider>();
                socialProviders.add(new SocialProvider(socialProviderName));
                newAcc.setAccSocialProviders(socialProviders);
                newAcc.setEnabled(true);

                try {
                    Account createdAcc = accountService.createAccount(newAcc);
                    if (createdAcc != null) {
                        // Acc created
                        SecurityUtil.logInUser(createdAcc);
                        providerSignInUtils.doPostSignUp(createdAcc.getEmail(), webRequest);
                        mav.setViewName("redirect:/main");
                        return mav;
                    }
                } catch (EmailExistsException eee) {
                    mav.addObject("error", messages.getMessage("social.registration.idExists", null, webRequest.getLocale()));
                    mav.setViewName("redirect:/login");
                }
            }

           /* Account newAcc = new Account();

            // TODO consider changing it to different id
            newAcc.setEmail(connection.getProfileUrl());

            newAcc.setPassword("SocialPassword");

            newAcc.setSignInProvider(SocialMediaEnum.valueOf(connection.getKey().getProviderId().toUpperCase()));
            newAcc.setPassword("SocialPassword");
            newAcc.setEnabled(true);
            try {
                Account acc = accountService.createAccount(newAcc);
                if (acc != null) {
                    // Acc created
                    String appUrl = request.getRequestURL().toString().split("/register")[0];
                    System.out.println("APP URL = " + appUrl);

                    SecurityUtil.logInUser(acc);
                    providerSignInUtils.doPostSignUp(acc.getEmail(), webRequest);
                    mav.setViewName("redirect:/main");
               }
            } catch(EmailExistsException eee) {
                mav.addObject("error", messages.getMessage("social.registration.idExists", null, webRequest.getLocale()));
                mav.setViewName("redirect:/login");
                //eee.printStackTrace();
            }*/

        } else {
            String message = messages.getMessage("social.sign.in.failure", null, webRequest.getLocale());
            mav.addObject("error", message);
            mav.setViewName("redirect:/login");
        }

        return mav;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView createAccount(@Valid @ModelAttribute RegistrationForm regForm,
                                      BindingResult bResult, HttpServletRequest request, WebRequest webRequest, Model m) {

        // Validate the form password & passConfirm match
        if (!regForm.getPassword().equals(regForm.getConfirmPassword())) {
            bResult.rejectValue("password", "registration.passwordMismatch");
            bResult.rejectValue("confirmPassword", "registration.passwordMismatch");
            System.out.println("Password match validation - failed.");
        }

        if (bResult.hasErrors()) {
            // Clean the password fields before returning the form again
            regForm.setPassword("");
            regForm.setConfirmPassword("");

            return new ModelAndView("registrationForm");
        }

        // Build up the Account entity
        Account newAcc = new Account();
        newAcc.setEmail(regForm.getEmail());
        newAcc.setPassword(regForm.getPassword());

        try {// Attempt acc creation
            Account acc = accountService.createAccount(newAcc);
            if (acc != null) {
                ModelAndView mav = new ModelAndView();
                // Acc created
                String appUrl = request.getRequestURL().toString().split("/register")[0];
                System.out.println("APP URL = " + appUrl);

                /*ProviderSignInUtils.handlePostSignUp(acc.getEmail(), request);*/
                emailService.sendConfirmationEmail(new OnRegistrationCompleteEvent(newAcc, request.getLocale(), appUrl));
                // Forward to the login, acknowleding that account has been created
                mav.addObject("msg", messages.getMessage("registration.created", null, request.getLocale()));
                mav.setViewName("redirect:/login");


                return mav;
            } else { // Acc not created
                bResult.rejectValue("email", "registration.accCreationError");
                // Clean the passwords fields
                regForm.setPassword("");
                regForm.setConfirmPassword("");
                return new ModelAndView("registrationForm");
            }
        }  catch(EmailExistsException eee) {
            System.out.println("Catched EmailexistsException, attaching the rejected value to BindingREsult");
            bResult.rejectValue("email", "registration.emailExists");
            // Clean the password fields before returning the form again
            regForm.setPassword("");
            regForm.setConfirmPassword("");
            return new ModelAndView("registrationForm");
        } catch (EmailNotSentException uee) {
            System.out.println("Catched EmailNotSentException, attaching the rejected value to BindingResult");
            bResult.rejectValue("email", "registration.emailCouldNotBeSent");
            return new ModelAndView("registrationForm");
        }
    }
}
