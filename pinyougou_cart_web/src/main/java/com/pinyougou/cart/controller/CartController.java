package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtil;
import com.pinyougou.pojogroup.Cart;
import com.sun.org.apache.regexp.internal.RE;
import entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    //添加购物车
    @RequestMapping("/addCartItem")
    public Result addCartItem(HttpServletRequest request, HttpServletResponse response, Long itemId, Integer num){
        //判断用户是否登录
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //如果是匿名用户 未登录
        try {
            //获取购物车列表
            List<Cart> cartList = findCartList(request, response);
            if(name.equals("anonymousUser")){
                //调用服务层方法将要添加的商品添加到购物车中
                cartList = cartService.addGoodsToCartList(cartList, itemId, num);
                //如果未登录, 存到cookie
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),7*24*3600,true);
                System.out.println("向cookie中存入数据"+name);
            }else{ //如果已登录, 存到redis
                //先从redis中获取之前的购物车列表
                List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);
                //将要添加的商品添加到这个购物车列表
                cartList = cartService.addGoodsToCartList(cartListFromRedis, itemId, num);
                //存到redis
                cartService.saveCartListToRedis(name,cartList);
                System.out.println("已登录, 数据存入redis");
            }
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    //查询购物车列表
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response){
        //判断用户是否登录
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //如果是匿名用户 未登录, 则从cookie中获取
        if(name.equals("anonymousUser")){
            String cartListStr = CookieUtil.getCookieValue(request, "cartList", true);
            List<Cart> cartList = new ArrayList<>();
            if(StringUtils.isNotBlank(cartListStr)){
                cartList=JSON.parseArray(cartListStr,Cart.class);
            }
            return cartList;
        }else{ //如果已登录, 则从redis数据库中获取
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);
            //再获取cookie中的购物车
            String cartListStr = CookieUtil.getCookieValue(request, "cartList", true);
            List<Cart> cookieCartList = new ArrayList<>();
            if(StringUtils.isNotBlank(cartListStr)){
                cookieCartList=JSON.parseArray(cartListStr,Cart.class);
            }
            if(cookieCartList!=null){
                //将cookie中的购物车合并到redis中
                List<Cart> cartList = cartService.mergeCartList(cookieCartList, cartListFromRedis);
                //合并后存到redis中
                cartService.saveCartListToRedis(name,cartList);
                cartListFromRedis = cartService.findCartListFromRedis(name);
                //删除cookie中的购物车
                CookieUtil.deleteCookie(request,response,"cartList");
            }
            return cartListFromRedis;
        }
    }
}
