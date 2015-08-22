package core.controller;

import core.authentication.AccountUserDetails;
import core.entity.Account;
import core.entity.Role;
import core.entity.Test;
import core.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Adrian on 27/06/2015.
 */
@Controller
public class MainController {

    @Autowired
    AccountService accountService;

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public ModelAndView main() {
        ModelAndView mav = new ModelAndView();

        AccountUserDetails accountUserDetails = (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("TEST(get) ->| acc : " + accountUserDetails.getUsername());

        Account acc = accountService.findAccount(accountUserDetails.getUsername());

        Test test = accountService.getTest(acc);

        if (test != null)
            mav.addObject("testWord", test.getTest_word());

        mav.setViewName("main");
        return mav;
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String test(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();

        AccountUserDetails accountUserDetails = (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("TEST(post) ->| acc : " + accountUserDetails.getUsername());

        Account acc = accountService.findAccount(accountUserDetails.getUsername());



        System.out.println("Request attribute testAttr = " + request.getParameter("testAttr"));

        accountService.setTest(acc, request.getParameter("testAttr"));

        mav.setViewName("redirect:/main");
        /*return mav;*/
        return "redirect:/main";
    }
}
