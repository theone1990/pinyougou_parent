package com.pinyougou.page.listener;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

public class GenHtmlListener implements MessageListener {
    @Autowired
    private ItemPageService pageService;

    @Override
    public void onMessage(Message message) {
        if(message instanceof ObjectMessage){
            try {
                ObjectMessage objectMessage= (ObjectMessage) message;
                Long[] ids = (Long[]) objectMessage.getObject();
                for (Long id : ids) {
                    pageService.genItemHtml(id);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
