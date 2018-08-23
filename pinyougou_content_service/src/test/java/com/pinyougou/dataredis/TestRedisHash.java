package com.pinyougou.dataredis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class TestRedisHash {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void setValue(){
        redisTemplate.boundHashOps("namehash").put("a","Tom");
        redisTemplate.boundHashOps("namehash").put("b","Jerry");
        redisTemplate.boundHashOps("namehash").put("c","Peter");
    }

    @Test
    public void getKeys(){
        Set s = redisTemplate.boundHashOps("namehash").keys();
        System.out.println(s);
    }

    @Test
    public void getValues(){
        List list = redisTemplate.boundHashOps("namehash").values();
        System.out.println(list);
    }

    @Test
    public void getValuesByKey(){
        Object o = redisTemplate.boundHashOps("namehash").get("b");
        System.out.println(o);
    }

    @Test
    public void removeValuesByKey(){
        redisTemplate.boundHashOps("namehash").delete("c");
    }
}
