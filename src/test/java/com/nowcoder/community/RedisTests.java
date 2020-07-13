package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    //访问以字符串为值得数据
    public void testStrings(){
        String redisKey = "test:Count";

        //存数据
        redisTemplate.opsForValue().set(redisKey,1);

        //获取数据并打印
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    //演示如何访问hash
    @Test
    public void testHashes(){
        String redisKey = "test:user";

        //存值
        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"username","zhangsan");

        //取值
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));
    }

    //演示如何访问列表
    @Test
    public void testLists(){
        String redisKey = "test:ids";

        //列表从左边进
        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        //获取第0个位置的数据
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        //获取从0到2的数据
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));

        //从左边弹出数据
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }

    //演示集合
    @Test
    public void redisSets(){
        String reidsKey = "test:teacher";

        redisTemplate.opsForSet().add(reidsKey,"刘备","张飞","关羽","赵云","刘禅","诸葛亮");

        System.out.println(redisTemplate.opsForSet().size(reidsKey));
        System.out.println(redisTemplate.opsForSet().pop(reidsKey));
        //统计数据
        System.out.println(redisTemplate.opsForSet().members(reidsKey));

    }

    //演示有序的集合
    @Test
    public void testSortedSets(){
        String redisKey = "test:students";

        redisTemplate.opsForZSet().add(redisKey,"唐僧",80);
        redisTemplate.opsForZSet().add(redisKey,"孙悟空",99);
        redisTemplate.opsForZSet().add(redisKey,"猪八戒",73);
        redisTemplate.opsForZSet().add(redisKey,"沙僧",70);
        redisTemplate.opsForZSet().add(redisKey,"白龙马",66);

        //zCard是统计里面一共有多少个数据；score是统计这个人的分数；rank表示排名，默认由小到大;rang由小到大取前三名
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"猪八戒"));//返回的是索引
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"猪八戒"));//更改排序为由大到小
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,2));
    }

    //公共的命令可以访问key
    @Test
    public void testKeys(){
        redisTemplate.delete("test:user");
        //判断这个key是不是还存在
        System.out.println(redisTemplate.hasKey("test:user"));

        //设置过期时间，指定过期单位为秒
        redisTemplate.expire("test:students",10, TimeUnit.SECONDS);
    }

    //多次访问同一个key
    @Test
    public void testBoundOperations(){
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        //之前累加需要传入key ，现在不需要
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());

    }

    //编程式事务
    @Test
    public void testTransaction(){
        Object obj =  redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";
                //启用事务
                operations.multi();

                operations.opsForSet().add(redisKey,"zhangsan");
                operations.opsForSet().add(redisKey,"lisi");
                operations.opsForSet().add(redisKey,"wangwu");

                //用redis管理事务的时候，中间不要做查询，无效，下面那句话没有用
                System.out.println(operations.opsForSet().members(redisKey));

                return operations.exec();
            }
        });
        System.out.println(obj);
    }
}
