package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;

import java.util.List;
import java.util.Map;

public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 导入商品数据到索引库
     */
    public void importItemData(){
        //1.从数据库查询数据
        TbItemExample example = new TbItemExample();
        example.createCriteria().andStatusEqualTo("1");//已审核
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        //2.设置规格对应的动态域
        for (TbItem item : tbItems) {
            String spec = item.getSpec();
            Map map = JSON.parseObject(spec,Map.class);//将spec字段中的json字符串转换成json
            item.setSpecMap(map);//给带注解的字段赋值
        }
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        SolrUtil solrUtil = context.getBean(SolrUtil.class);
        solrUtil.importItemData();
    }
}
