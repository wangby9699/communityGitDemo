package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    //@Autowired
    //private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    //查询用户
    public User findUserById(int id){
        //return userMapper.selectById(id);
        //首先从cache中查询user
        User user = getCache(id);
        if(user == null){
            user = initCache(id);
        }
        return user;
    }

    //注册方法，返回形式以集合的样子给出
    public Map<String,Object> register(User user){
        Map<String,Object> map=new HashMap<>();
        //对空值进行判断
        if (user==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //账号为空
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空!");
            return map;
        }
        //密码为空
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空!");
            return map;
        }
        //邮箱为空
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emaildMsg","邮箱不能为空!");
            return map;
        }

        //验证账号
        User u=userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","账号已存在!");
            return map;
        }
        //验证邮箱
        u=userMapper.selectByEmail(user.getEmail());
        if (u!=null){
            map.put("emailMsg","邮箱已被注册！!");
            return map;
        }

        //经过上述判断，若没问题，则将注册者写进数据库
        //生成随机字符串，长度为0-5
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        //将原本密码与随机生成的字符串整合，再加密即可增大安全性
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());//激活码
        //随机生成头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //激活邮件
        Context context=new Context();
        context.setVariable("email",user.getEmail());
        //希望的路径   http://localhost:8080/community/activation/101/code
        String url=domain+contextPath+"/activation/"+user.getId() +"/"+user.getActivationCode();
        context.setVariable("url",url);
        //生成邮件模板内容
        String content=templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    /*
    激活方法
     */
    public int activation(int userId,String code){
        User user=userMapper.selectById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            //如果激活码匹配，就将状态改为激活
            userMapper.updateStatus(userId,1);
            //清理掉缓存
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }


    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map=new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("username","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("password","密码不能为空");
            return map;
        }

        //验证账号
        User user=userMapper.selectByName(username);
        if (user==null){
            map.put("usernameMsg","该账号不存在");
            return map;
        }
        //验证状态
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }
        //验证密码
        password=CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确！");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        //loginTicketMapper.insertLoginTicket(loginTicket);

        //存到redis中
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }


    //退出功能
    public void logout(String ticket){
        // loginTicketMapper.updateStatus(ticket,1);

        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        //表示删除
        loginTicket.setStatus(1);
        //再存回去
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }

    //查询凭证的代码
    public LoginTicket findLoginTicket(String ticket){
        //return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    //更新修改头像的路径
    public int updateHeader(int userId, String headerUrl){
        // return  userMapper.updateHeader(userId,headerUrl);
        //先更新
        int rows = userMapper.updateHeader(userId,headerUrl);
        clearCache(userId);
        return rows;

    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }

    //1.优先从缓存中取值
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    //2.取不到时，初始化缓存数据
    public User initCache(int userId){
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }

    //3.数据变更时清除缓存数据
    public void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

}
