package com.mooc.dao;

import org.junit.Test;
import redis.clients.jedis.Jedis;

public class RedisLinkTest {
    //@Test表示这个方法是单元测试的方法
      //连接并添加String类型数据
    @Test
      public void fun1() {
                //直接连接redis数据库
               Jedis jedis = new Jedis("127.0.0.1",6379);
                //设置连接密码
               jedis.auth("root");
               //添加String类型数据
                 jedis.set("field1","i am field1");
                //输出添加的数据（根据键，输出对应的值）
                 System.out.println(jedis.get("field1"));
                //删除String类型数据（根据键删除）
                 //jedis.del("field1");
                 //输出数据，查看是否删除成功
                 //System.out.println(jedis.get("field1"));
            }
}
