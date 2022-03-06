package com.kdy.live.bean.util.system;

public interface ProcessManageFactoryIF {
	public boolean checkPID(String pid);
	
	public void killProcess(String pid);

}
