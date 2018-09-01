package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.List;
import java.util.Map;

public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemService itemService;

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        //接收消息, 更新索引库
        if(message instanceof ObjectMessage){
            ObjectMessage objectMessage= (ObjectMessage) message;
            //接收消息, ids
            try {
                Long[] ids=(Long[])objectMessage.getObject();
                //根据id从数据库中获取商品的数据
                List<TbItem> items = itemService.findItemsByIds(ids);
                //更新索引库, 设置规格对应的动态域
                for (TbItem item : items) {
                    String spec = item.getSpec();
                    Map map = JSON.parseObject(spec, Map.class);
                    item.setSpecMap(map);
                }
                solrTemplate.saveBeans(items);
                solrTemplate.commit();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
