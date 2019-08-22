package com.it.contorller;

import com.alibaba.fastjson.JSONObject;
import com.it.entity.TextMessage;
import com.it.utils.CheckUtil;
import com.it.utils.HttpClientUtil;
import com.it.utils.XmlUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

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
        System.out.println(echostr);
        return echostr;
    }

    @RequestMapping(value = "/dispatCherServlet", method = RequestMethod.POST)
    public void getDispatCherServlet(HttpServletRequest request, HttpServletResponse response, String signature,
                                     String timestamp, String nonce, String echostr) throws Exception {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        Map<String, String> result = XmlUtils.parseXml(request);
        String toUserName = result.get("ToUserName");
        String fromUserName = result.get("FromUserName");
        //消息类型
        String msgType = result.get("MsgType");
        //输入的文字
        String content = result.get("Content");
        //语音识别出的文字
        String recognition = result.get("Recognition");

        switch (msgType) {
            case "text":
                String resultXml = null;
                PrintWriter out = response.getWriter();
                TextMessage textMessage = new TextMessage();
                textMessage.setToUserName(fromUserName);
                textMessage.setFromUserName(toUserName);
                textMessage.setCreateTime(System.currentTimeMillis());
                textMessage.setMsgType("text");
                if ("你好".equals(content.substring(0,content.length()-1))) {

                    textMessage.setContent("你也好~~~");
                } else {
                    qingYunKe(content, textMessage);
                }

                resultXml = XmlUtils.messageToXml(textMessage);
                out.println(resultXml);
                out.close();
                break;
            case "voice":
                String voiceResultXml = null;
                PrintWriter outVoice = response.getWriter();
                TextMessage textMessageVoice = new TextMessage();
                textMessageVoice.setToUserName(fromUserName);
                textMessageVoice.setFromUserName(toUserName);
                textMessageVoice.setCreateTime(System.currentTimeMillis());
                textMessageVoice.setMsgType("text");
                if ("你好。".equals(recognition)) {

                    textMessageVoice.setContent("瞎BB什么~~~");
                } else {
                    qingYunKe(content, textMessageVoice);
                }

                voiceResultXml = XmlUtils.messageToXml(textMessageVoice);
                outVoice.println(voiceResultXml);
                outVoice.close();
                break;
            default:
                break;
        }

    }

    private void qingYunKe(String content, TextMessage textMessageVoice) {
        String resultAPiStr = HttpClientUtil
                .doGet("http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + content);
        JSONObject jsonObject = new JSONObject().parseObject(resultAPiStr);
        Integer state = jsonObject.getInteger("result");
        if (state != null) {
            String contentAPi = jsonObject.getString("content");
            textMessageVoice.setContent(contentAPi);
        }
    }
}
