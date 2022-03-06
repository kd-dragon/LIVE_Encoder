package com.kdy.live.bean.util.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.kdy.live.dto.LiveSchedMemoryVO;

@Component
public class ProcessManageFactory {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final LiveSchedMemoryVO mvo;
	
	public ProcessManageFactory(LiveSchedMemoryVO mvo) {
		this.mvo = mvo;
	}
	
	public ProcessManageFactoryIF template() {
		logger.info(">> osType : " + mvo.getOsType());
		
		ApplicationContext ctx = mvo.getApplicationContext();
		
		if(mvo.getOsType().equalsIgnoreCase("window")) {
			return ctx.getBean("processManagerWindow", ProcessManageFactoryIF.class);
		} else {
			return ctx.getBean("processManagerLinux", ProcessManageFactoryIF.class);
		}
	}
}
