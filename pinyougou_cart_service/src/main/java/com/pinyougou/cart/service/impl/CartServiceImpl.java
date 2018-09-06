package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.sellergoods.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ItemService itemService;
    @Autowired
    private RedisTemplate redisTemplate;

    //添加商品到购物车
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //根据要添加的商品id获取商品数据tbItem
        TbItem tbItem = itemService.findOne(itemId);
        //获取sellerId
        String sellerId = tbItem.getSellerId();
        //判断购物车列表中是否包含该商家
        Cart cart = isCartListContainsSellerId(cartList,sellerId);
        if(cart!=null){ //包含
            //获取商品明细列表
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            //判断明细列表中是否包含该商品id
            TbOrderItem orderItem = isOrderItemListContainsItemId(orderItemList,itemId);
            if(orderItem!=null){
                //包含, 数量相加
                orderItem.setNum(orderItem.getNum()+num);
                //该商品总金额重新计算
                double totalfee = orderItem.getPrice().doubleValue()*orderItem.getNum();
                orderItem.setTotalFee(new BigDecimal(totalfee));
                //如果数量为0, 就删除这个明细
                if(orderItem.getNum()==0){
                    orderItemList.remove(orderItem);
                }
                //如果orderItemList长度为0, 就删除这个商家的购物车对象
                if(orderItemList.size()==0){
                    cartList.remove(cart);
                }
            }else{
                //该商家的明细列表中不包含这个商品, 则添加一个商品明细
                orderItem = createOrderItem(tbItem,num);
                orderItemList.add(orderItem);
            }
        }else{
            //购物车列表中不包含该商家, 则新建一个购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(tbItem,num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);
        }
        return cartList;
    }

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车数据"+username);
        List<Cart> cartList= (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if(cartList==null){
            cartList=new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis中存入购物车数据"+username);
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cookieList, List<Cart> redisList) {
        for (Cart cart : cookieList) { //遍历cookie中的购物车列表,遍历列表的订单项列表,得到每一个订单项, 再添加到redisList中
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                redisList = addGoodsToCartList(redisList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redisList;
    }

    private TbOrderItem createOrderItem(TbItem tbItem, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setItemId(tbItem.getId());
        orderItem.setGoodsId(tbItem.getGoodsId());
        orderItem.setTitle(tbItem.getTitle());
        orderItem.setPrice(tbItem.getPrice());
        orderItem.setNum(num);
        double v = tbItem.getPrice().doubleValue()*num;
        orderItem.setTotalFee(new BigDecimal(v));
        orderItem.setPicPath(tbItem.getImage());
        orderItem.setSellerId(tbItem.getSellerId());
        return orderItem;
    }

    //判断明细列表中是否包含该商品id
    private TbOrderItem isOrderItemListContainsItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().longValue()==itemId){ //有风险, 两个对象是否相等
                return orderItem;
            }
        }
        return null;
    }

    //判断购物车列表中是否包含该商家
    private Cart isCartListContainsSellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
}
