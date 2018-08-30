package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.swing.UIManager.get;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String,Object> resultMap = new HashMap<>();
        //按关键词查询并高亮显示
        Map map = searchList(searchMap);
        //按关键词查询商品分类
        Map map1 = searchCategoryList(searchMap);
        resultMap.putAll(map);
        resultMap.putAll(map1);
        //规格和品牌列表查询 如果不点击 默认使用第一个分类名称
        List<String> categoryList = (List<String>) map1.get("categoryList");
        String category = (String) searchMap.get("category");
        if(StringUtils.isNotBlank(category)){
            Map map2 = searchBrandAndSpecList(category);
            resultMap.putAll(map2);
        }else{
            Map map2 = searchBrandAndSpecList(categoryList.get(0));
            resultMap.putAll(map2);
        }
        return resultMap;
    }

    /**
     * 查询列表结果(高亮)
     */
    private Map searchList(Map searchMap){
        Map map = new HashMap();
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮的域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
        highlightOptions.setSimplePostfix("</em>");//高亮后缀
        query.setHighlightOptions(highlightOptions);//设置高亮选项
        //按照关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //过滤查询---按分类
        if(!"".equals(searchMap.get("category"))){
            Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //过滤查询---按品牌
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //过滤查询---按规格
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap= (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria=new Criteria("item_spec_"+key).is(specMap.get(key));
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //按价格筛选
        String price = (String) searchMap.get("price");
        if(StringUtils.isNotBlank(price)){
            String[] split = price.split("-");
            Criteria criteria1 = new Criteria("item_price");
            if(split[1].equals("*")){
                criteria1.greaterThanEqual(split[0]);
            }else{
                criteria1.between(split[0],split[1],true,true);
            }
            FilterQuery filterQuery=new SimpleFilterQuery(criteria1);
            query.addFilterQuery(filterQuery);
        }

        //分页查询
        Integer pageNo= (Integer) searchMap.get("pageNo");
        if(pageNo==null){
            pageNo=1;//默认第一页
        }
        Integer pageSize= (Integer) searchMap.get("pageSize");
        if(pageSize==null){
            pageSize=20;
        }
        query.setOffset((pageNo-1)*pageSize);//从第几条记录查询
        query.setRows(pageSize);

        //排序过滤(1.设置排序的字段, 2.设置升序/降序)
        String sortField= (String) searchMap.get("sortField");
        String sortType= (String) searchMap.get("sortType");
        if(StringUtils.isNotBlank(sortField) && StringUtils.isNotBlank(sortType)){
            if(sortType.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }else{
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
        }

        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        for (HighlightEntry<TbItem> entry : page.getHighlighted()) {//循环高亮入口集合
            TbItem item = entry.getEntity();//获取原实体类
            if(entry.getHighlights().size()>0 && entry.getHighlights().get(0).getSnipplets().size()>0){
                item.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));//设置高亮结果
            }
        }
        map.put("rows",page.getContent());
        map.put("total",page.getTotalElements());//返回总记录数
        map.put("totalPages",page.getTotalPages());//返回总页数
        return map;
    }

    /**
     * 根据搜索关键词查询商品分类列表
     */
    private Map searchCategoryList(Map searchMap){
        Map map = new HashMap();
        List<String> categoryList=new ArrayList<>();
        String keywords= (String) searchMap.get("keywords");
        Query query = new SimpleQuery();
        //按照关键词查询
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            categoryList.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
        }
        map.put("categoryList",categoryList);
        return map;
    }

    /**
     * 查询品牌和规格列表
     */
    private Map searchBrandAndSpecList(String category){
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获取模板id
        if(typeId!=null){
            //根据模板id查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList",brandList);
            //根据模板id查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);
        }
        return map;
    }
}
