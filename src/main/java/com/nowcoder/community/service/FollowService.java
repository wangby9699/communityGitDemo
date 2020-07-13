package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    //关注
    public void follow(int userId, int entityType, int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                //启用事务
                operations.multi();

                //中间做两次存储操作
                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    //取消关注
    public void unfollow(int userId, int entityType, int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                //启用事务
                operations.multi();

                //中间做两次存储操作
                operations.opsForZSet().remove(followeeKey,entityId);
                operations.opsForZSet().remove(followerKey,userId);

                return operations.exec();
            }
        });
    }

    //查询关注目标、关注的实体的数量
    public long findFolloweeCount(int userId, int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        //zcard表示统计zset的数量
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    //查询实体的粉丝数量
    public long findFollowerCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //查询当前用户是否关注该实体
    public boolean hasFollowed(int userId, int entityType, int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        //String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        //判断followeeKey中entityId的分数，看是否在
        return redisTemplate.opsForZSet().score(followeeKey,entityId) != null ;
    }

    //查询用户关注的人
    public List<Map<String,Object>> findFollowees(int userId,int offset, int limit){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey,offset,offset + limit - 1);

        if (targetIds == null){
            return null;
        }

        List<Map<String,Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds){
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);

            //查询关注的时间,得到的小数是时间毫秒数转化来的
            Double score = redisTemplate.opsForZSet().score(followeeKey,targetId);
            map.put("followTime",new Date(score.longValue()));

            list.add(map);
        }
        return list;
    }

    //查询某用户的粉丝
    public List<Map<String,Object>> findFollowers(int userId, int offset, int limit){
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey,offset,offset + limit - 1);

        if (targetIds == null){
            return null;
        }
        List<Map<String,Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds){
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);

            //查询关注的时间,得到的小数是时间毫秒数转化来的
            Double score = redisTemplate.opsForZSet().score(followerKey,targetId);
            map.put("followTime",new Date(score.longValue()));

            list.add(map);
        }
        return list;
    }
}
