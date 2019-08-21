package com.it.contorller;

import com.it.utils.CheckUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author User
 */
@RestController
public class DispatCherServlet {

    @RequestMapping(value = "/dispatCherServlet", method = RequestMethod.GET)
    public String getDispatCherServlet(String signature, String timestamp, String nonce, String echostr) {
        boolean checkSignature = CheckUtil.checkSignature(signature, timestamp, nonce);
        if (!checkSignature) {
            return null;
        }
        return echostr;
    }
}
