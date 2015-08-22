package core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Adrian on 09/05/2015.
 */
@Controller
@RequestMapping(value = "/index")
public class IndexController {

    @RequestMapping(method = RequestMethod.GET)

    public String index() {
        final Log logger = LogFactory.getLog(getClass());
        if (logger != null) {
            System.out.println("Logger exists -> class = " + logger.getClass().toString());

            System.out.println("Debug is " + ((logger.isDebugEnabled()) ? "enabled" : "disabled" ));
        } else
            System.out.println("Logger does not exist");
        return "index";
    }
}
