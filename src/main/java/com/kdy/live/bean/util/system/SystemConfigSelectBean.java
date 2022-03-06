package com.kdy.live.bean.util.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kdy.live.dao.live.LiveManageDAOFactory;
import com.kdy.live.dto.system.SystemConfigVO;

@Component
public class SystemConfigSelectBean {
	private Logger logger = LoggerFactory.getLogger(SystemConfigSelectBean.class);
	
	private final LiveManageDAOFactory liveManageDAOFactory;
	
	@Autowired
	public SystemConfigSelectBean(LiveManageDAOFactory liveManageDAOFactory) {
		this.liveManageDAOFactory = liveManageDAOFactory;
	}
	
	public SystemConfigVO getSystemConfig() throws Exception {
		return liveManageDAOFactory.getDAO().selectSystemConfig();
	}

}
