package com.nowcoder.community;

import org.aspectj.lang.annotation.Aspect;
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

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;


    // 字符串
    @Test
    public void testStrings() {
        String key = "test:count";

        // 存数据
        redisTemplate.opsForValue().set(key, 1);
        // 取数据
        System.out.println(redisTemplate.opsForValue().get(key));
        System.out.println(redisTemplate.opsForValue().increment(key));
        System.out.println(redisTemplate.opsForValue().decrement(key));

    }


    // 哈希值
    @Test
    public void testHash() {
        String key = "test:hash";

        // 存数据
        redisTemplate.opsForHash().put(key, "name","zhangsan");
        redisTemplate.opsForHash().put(key,"id", 1);
        // 取数据
        System.out.println(redisTemplate.opsForHash().get(key,"id"));
        System.out.println(redisTemplate.opsForHash().get(key,"name "));

    }


    // 列表
    @Test
    public void testList() {
        String key = "test:ids";
        redisTemplate.opsForList().leftPush(key, 100);
        redisTemplate.opsForList().leftPush(key, 101);
        redisTemplate.opsForList().leftPush(key, 102);
        redisTemplate.opsForList().leftPush(key, 202);

        System.out.println(redisTemplate.opsForList().size(key));
        System.out.println(redisTemplate.opsForList().index(key,1));
        System.out.println(redisTemplate.opsForList().range(key,1,2));
        System.out.println(redisTemplate.opsForList().rightPop(key));
        System.out.println(redisTemplate.opsForList().leftPop(key));
        System.out.println(redisTemplate.opsForList().size(key));
    }

    // 集合
    @Test
    public void testSet() {
        String key = "test:set";
        redisTemplate.opsForSet().add(key,1);
        redisTemplate.opsForSet().add(key,2);
        redisTemplate.opsForSet().add(key,3,4,5);

        System.out.println(redisTemplate.opsForSet().size(key));
        System.out.println(redisTemplate.opsForSet().pop(key));
        System.out.println(redisTemplate.opsForSet().members(key));


    }

    // 有序集合
    @Test
    public void testSortSet() {
        String key = "test:SortSet";

        redisTemplate.opsForZSet().add(key,"张三",100);
        redisTemplate.opsForZSet().add(key,"李四",20);
        redisTemplate.opsForZSet().add(key,"王五",10);
        redisTemplate.opsForZSet().add(key,"刘六",50);
        redisTemplate.opsForZSet().add(key,"西西",20);

        System.out.println(redisTemplate.opsForZSet().zCard(key));
        System.out.println(redisTemplate.opsForZSet().score(key,"张三"));
        System.out.println(redisTemplate.opsForZSet().rank(key,"张三") + 1);
        System.out.println(redisTemplate.opsForZSet().range(key,0,2));
        System.out.println(redisTemplate.opsForZSet().reverseRange(key,0,2));

    }

    @Test
    public void testKey() {

        System.out.println(redisTemplate.hasKey("test:set"));
        // 删除key
        redisTemplate.delete("test:set");

        System.out.println(redisTemplate.hasKey("test:set"));

        redisTemplate.expire("test:ids", 10, TimeUnit.SECONDS);
    }

    // 多次访问同一个key
    @Test
    public void testBoundOperations() {
        String key = "test:cont";
        BoundValueOperations operations = redisTemplate.boundValueOps(key);

        operations.set(5);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());

    }

    // 编程式事物
    @Test
    public void testTransactional() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String key = "test:tx";

                // 在提交任务之前，是不会执行代码的，所以在此之前立刻查询不起作用
                operations.multi();

                operations.opsForSet().add(key, "111");
                operations.opsForSet().add(key, "Wangwu");
                operations.opsForSet().add(key, "Liuliu");

                System.out.println(operations.opsForSet().members(key));

                return operations.exec();
            }
        });
        System.out.println(obj);
    }



}


