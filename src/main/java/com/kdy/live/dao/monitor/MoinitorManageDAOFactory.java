package com.kdy.live.dao.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.kdy.live.dto.LiveSchedMemoryVO;

@Component
public class MoinitorManageDAOFactory {
	
private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${spring.daoMode}")
	private String daoMode;
	
	@Autowired
	private LiveSchedMemoryVO mvo;
	
	public MonitorManageDAOFactoryIF getDAO() {
		logger.debug(">> daoMode : " + daoMode);
		
		ApplicationContext ctx = mvo.getApplicationContext();
		
		/*
		 * String[] beanNames = ctx.getBeanDefinitionNames(); for(String m: beanNames) {
		 * logger.info(m); }
		 */
		
		if(daoMode.equals("N")) {
			return ctx.getBean("monitorManageDBDAO", MonitorManageDAOFactoryIF.class);
		} else {
			return ctx.getBean("monitorManageWEBDAO", MonitorManageDAOFactoryIF.class);
		} 
	}

}
