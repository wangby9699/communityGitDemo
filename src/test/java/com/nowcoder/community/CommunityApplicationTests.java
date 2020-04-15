package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	//程序运行的时候，applicationContext会自动记录，我就可以在其它地方使用了
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}

	@Test
	public void testApplicationContext(){
		System.out.println(applicationContext);

		//从applicationContext获取自动装配的bean
		AlphaDao alphaDao=applicationContext.getBean(AlphaDao.class);
		//调用查询方法，并且将结果输出
		System.out.println(alphaDao.select());

		//强制类型转换也可，在后面写上AlphaDao.class也可
		alphaDao=applicationContext.getBean("alphaHibernate",AlphaDao.class);
		System.out.println(alphaDao.select());
	}

	@Test
	public void testBeanManagement(){
		AlphaService alphaService=applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);

		/*这两个是对实例化两次，即被容器管理的bean，是默认使用单例的
		alphaService=applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);

		 */

	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat=applicationContext.getBean(SimpleDateFormat.class);
		//格式化当前日期并输出
		System.out.println(simpleDateFormat.format(new Date()));
	}

	@Autowired //希望spring整个容器把AlphaDao注入给alphaDao这个属性
	@Qualifier("alphaHibernate")//spring容器就会把这个alphaHibernate的bean注入给alphaDao,不再管@Primary
	private  AlphaDao alphaDao;

	@Autowired
	private AlphaService alphaService;

	@Autowired
	private SimpleDateFormat simpleDateFormat;

	@Test
	public void testDI(){
		System.out.println(alphaDao);
		System.out.println(alphaService);
		System.out.println(simpleDateFormat);
	}
}
