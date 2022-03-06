package com.kdy.live.bean.util.system;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProcessManagerLinux implements ProcessManageFactoryIF{
	
	private Logger logger = LoggerFactory.getLogger(ProcessManagerLinux.class);

	@Override
	public boolean checkPID(String pid) {
		Process p = null;
		ArrayList<String> pidList = new ArrayList<String>();
		boolean isExist = false;
		
		try {
			//p = Runtime.getRuntime().exec(new String[] { "sh", "-c", "ps -f "+pid+" | awk '{print $2}'" });
			p = Runtime.getRuntime().exec(new String[] { "sh", "-c", "ps -f "+pid+" | grep ffmpeg | awk '{print $2}'" });
			BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while((line = bfr.readLine()) != null) {
				logger.info("[Check Process ID] :: " + line);
				if(line.equalsIgnoreCase("PID")) {
					continue;
				}
				pidList.add(line);
			}
			bfr.close();
			bfr = null;
			
			if(pidList.size() > 0) {
				isExist = true;
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			for(StackTraceElement st : e.getStackTrace()) {
				if(st.toString().startsWith("com.kdy.live")) {
					logger.error(st.toString());
				}
			}
		}
		return isExist;
	}

	@Override
	public void killProcess(String pid) {
		Process p = null;
		ArrayList<String> pidList = new ArrayList<String>();
		
		try {
			String[] commandLine = new String[] {"sh", "-c", "ps -f " + pid + " | grep ffmpeg | awk '{print \"kill -9 \"$2}'"};
			
			p = Runtime.getRuntime().exec(commandLine);
			BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while((line = bfr.readLine()) != null) {
				logger.info("[Kill Command] :: " + line);
				if(line.equalsIgnoreCase("PID")) {
					continue;
				}
				pidList.add(line);
			}
			bfr.close();
			bfr = null;
			
			for(int i=0; i<pidList.size(); i++) {
				Runtime.getRuntime().exec(pidList.get(i));
			}
			
			checkPID(pid);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			for(StackTraceElement st : e.getStackTrace()) {
				if(st.toString().startsWith("com.kdy.live")) {
					logger.error(st.toString());
				}
			}
		}
	}

}
