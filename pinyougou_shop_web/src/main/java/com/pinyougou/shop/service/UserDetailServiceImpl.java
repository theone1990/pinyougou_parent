package com.pinyougou.shop.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证类
 */
public class UserDetailServiceImpl implements UserDetailsService {
    @Reference
    private SellerService sellerService; //引入sellerService从数据库查询账号密码校验

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //构建角色列表
        System.out.println("进过了UserDetailServiceImpl");
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //查询商家对象
        TbSeller seller = sellerService.findOne(username);
        if(seller != null){
            if(seller.getStatus().equals("1")){
                return new User(username,seller.getPassword(),grantedAuthorities);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
}
