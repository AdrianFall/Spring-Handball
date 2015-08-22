package web.authentication.form;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by Adrian on 18/05/2015.
 */
public class FormSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        handle(request,response,authentication);

        if (savedRequest == null) {
            clearAuthenticationAttributes(request);
            return;
        }

        String targetUrlParam = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl() || targetUrlParam != null && StringUtils.hasText(request.getParameter(targetUrlParam))) {
            requestCache.removeRequest(request, response);
            clearAuthenticationAttributes(request);
            return;
        }
        clearAuthenticationAttributes(request);
    }

    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{
        String targetUrl = determineTargetUrl(authentication);

        redirectStrategy.sendRedirect(request, response, targetUrl);

    }

    protected String determineTargetUrl(Authentication authentication) {
        boolean isUser = false;
        boolean isAdmin = false;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                isAdmin = true;
                break;
            } else if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
                isUser = true;
                break;
            }
        }

        if (isAdmin) {
            return "/admin.html";
        } else if (isUser) {
            return "/main.html";
        } else {
            throw new IllegalStateException();
        }
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    @Override
    public RedirectStrategy getRedirectStrategy() { return redirectStrategy; }

    @Override
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) { this.redirectStrategy = redirectStrategy; }
}
