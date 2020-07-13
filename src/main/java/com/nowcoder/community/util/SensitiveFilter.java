package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点初始化
    private TrieNode rootNode = new TrieNode();

    //注解表示初始化方法，表示容器在调用（实例化,服务启动就会自动调用）SensitiveFilter这个bean之后，程序会自动调用init()方法
    @PostConstruct
    public void init(){
        try(
                //获取类加载器
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //字符流转换成缓冲流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is)) ;
                ){
            //读取
            String keywords;
            while((keywords = reader.readLine()) != null){
                //读到了敏感词，则应该把敏感词添加到前缀中去
                this.addKeyword(keywords);
            }
        }catch (IOException e){
            logger.error("加载敏感词文件失败：",e.getMessage());
        }

    }


    //将一个敏感词添加到前缀树中去
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++){
            char c=keyword.charAt(i);//每次遍历都能得到一个字符
            //试图找一下当前节点有没有子节点
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null){
                //如果为空,初始化子节点
                subNode = new TrieNode();
                //把子节点挂到当前节点的下面
                tempNode.addSubNode(c,subNode);
            }
            //让指针指向子节点，进行下一轮的循环
            tempNode = subNode;

            //设置结束的标识
            if (i == keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /*
    过滤敏感词
    参数是待过滤文本 @param  text
    返回的是过滤后的文本  @return
     */
    public String filter(String text){
        if (StringUtils.isBlank(text)){//参数为空，直接null
            return null;
        }

        //指针1，指向树
        TrieNode tempNode = rootNode;
        //指针2  索引，所以是int
        int begin = 0;
        //指针3
        int position = 0;
        //结果，变长字符串
        StringBuilder sb = new StringBuilder();

        //用指针3可能结束比较早，效率比较高点
        while (position < text.length()){
            char c = text.charAt(position);//得到当前某一个字符

            //跳过符号
            if (isSymbol(c)){
                //若指针1处于根节点，将此符号记入结果，让指针2向下走一步
                if (tempNode == rootNode){
                    sb.append(c);//把获取到的字符串加入待输出的sb中
                    begin++;
                }
                //无论符号在开头或中间，指针3都向下走一步
                position++;
                continue;
            }
            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
                //以begin为开头的字符串不是敏感词，则记录
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //重新指向根节点
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){
                //发现敏感词，将begin~position字符串替换掉
                sb.append(REPLACEMENT);
                //指针进入下一个位置,此时让position后移一步，然后让begin与其位置相同
                begin = ++position;
                //重新指向根节点
                tempNode = rootNode;
            }else{
                //检查下一个字符
                position++;
            }
        }

        //将最后一批字符计入结果
        sb.append(text.substring(begin));

        return sb.toString();//最后结果

    }

    //判断是否为符号
    private boolean isSymbol(Character c){
        //判断字符是不是普通字符，是为true  0x2E80~0x9FFF视为东亚的文字范围，不作为特殊符号，范围之外才是特殊符号
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 ||c > 0x9FFF);
    }


    //定义前缀树
    private class TrieNode {

        //关键词结束的标识
        private boolean isKeywordEnd = false;

        //当前节点的子节点(key是下级字符，value是下级节点)
        private Map<Character,TrieNode>  subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c,node);
        }

        //获取子节点的办法
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }


    }
}
