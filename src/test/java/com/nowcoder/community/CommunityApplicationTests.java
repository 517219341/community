package com.nowcoder.community;

import com.nowcoder.community.CommunityApplication;
import com.nowcoder.community.config.AlphaConfig;
import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext(){
		System.out.println(applicationContext);
		//通过默认方式获得Bean中的类对象: @Repository    @Primary
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());
		//通过指定的方式,即字符搜索实现相关接口的类：@Repository("alphaDaoHibernate")
		//alphaDao = applicationContext.getBean("alphaDaoHibernate",AlphaDao.class);
		alphaDao = (AlphaDao)applicationContext.getBean("alphaDaoHibernate");
		System.out.println(alphaDao.select());

	}

	@Test
	public void testBeanManagement(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	//更简单的依赖注入方式：不需要主动的去从BEAN中获取数据。即不需要主动使用反射机制
	@Autowired
	private AlphaDao alphaDao;

	@Autowired
	@Qualifier("alphaDaoHibernate")		//获取指定的类对象
	private AlphaDao alphaDao1;

	@Test
	public void TestDI(){
		System.out.println(alphaDao.select());
		System.out.println(alphaDao1.select());
	}
}
