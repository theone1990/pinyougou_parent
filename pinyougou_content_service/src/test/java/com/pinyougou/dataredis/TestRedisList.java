package com.pinyougou.dataredis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class TestRedisList {

    @Autowired
    private RedisTemplate redisTemplate;
    //右压栈和左压栈
    @Test
    public void setValue(){
        /*redisTemplate.boundListOps("namelist1").rightPush("刘备");
        redisTemplate.boundListOps("namelist1").rightPush("关羽");
        redisTemplate.boundListOps("namelist1").rightPush("张飞");*/
        redisTemplate.boundListOps("namelist2").leftPush("刘备");
        redisTemplate.boundListOps("namelist2").leftPush("关羽");
        redisTemplate.boundListOps("namelist2").leftPush("张飞");
    }

    @Test
    public void getValue(){
        List list = redisTemplate.boundListOps("namelist2").range(0, -1);
        System.out.println(list);
    }

    @Test
    public void searchByIndex(){
        String s = (String) redisTemplate.boundListOps("namelist1").index(0);
        System.out.println(s);
    }

    @Test
    public void removeByIndex(){
        redisTemplate.boundListOps("namelist1").remove(1,"关羽");
    }
}
