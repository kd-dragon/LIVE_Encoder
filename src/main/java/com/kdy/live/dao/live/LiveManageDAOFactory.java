package com.kdy.live.dao.live;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.kdy.live.dto.LiveSchedMemoryVO;

@Component
@RequiredArgsConstructor
public class LiveManageDAOFactory {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${spring.daoMode}")
	private String daoMode;

	private final LiveSchedMemoryVO mvo;
	
	public LiveManageDAOFactoryIF getDAO() {
		logger.debug(">> daoMode : " + daoMode);
		
		ApplicationContext ctx = mvo.getApplicationContext();
		
		if(daoMode.equals("N")) {
			return ctx.getBean("liveManageDBDAO", LiveManageDAOFactoryIF.class);
		} else {
			return ctx.getBean("liveManageWEBDAO", LiveManageDAOFactoryIF.class);
		} 
	}
}
