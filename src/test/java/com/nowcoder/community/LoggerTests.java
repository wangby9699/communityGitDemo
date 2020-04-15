package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTests {
    //getLogger传入的名字就是类的名字
    private static final Logger logger= LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void testLogger(){
        //只是看logger的名字
        System.out.println(logger.getName());

        //打印日志
        logger.debug("debug log");
        logger.info("info log");
        logger.warn("warn log");
        logger.error("error log");
    }
}
