package core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Adrian on 29/06/2015.
 */
@Controller
public class AdminController {

    @RequestMapping(value =  "/admin")
    public String adminPage() {
        return "admin";
    }
}
