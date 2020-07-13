package com.nowcoder.community.util;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    //生成随机字符串
    public static String generateUUID(){
        //把生成的-全部替换为空
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密
    //只能加密，不能解密  hello->abc123def456
    //hello+3e4a8  -> abc123def456abc   随机加一些随机数，使得加密后的密码没有规律，不容易破解
    public static String md5(String key){
        //密码为空就不处理
        if (StringUtils.isBlank(key)){
            return null;
        }
        //调用工具，把结果加密成十六进制的字符串再返回
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        //传入的参数放到json中去
        json.put("code",code);
        json.put("msg",msg);
        if (map != null){
            for (String key : map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }

    //下面两个函数是对上述函数的重载，防止在传入数据不完整时仍旧可以调用
    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }

    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }


    //main方法测试
    public static void main(String[] args) {
        Map<String , Object> map = new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",25);
        System.out.println(getJSONString(0,"OK",map));
    }

}
