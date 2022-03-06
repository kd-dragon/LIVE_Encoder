package com.kdy.live.bean.util.system;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProcessManagerWindow implements ProcessManageFactoryIF {
	
	private Logger logger = LoggerFactory.getLogger(ProcessManagerWindow.class);
	
	@Override
	public boolean checkPID(String pid) {
		Process p = null;
		ArrayList<String> pidList = new ArrayList<String>();
		boolean isExist = false;
		
		try {
			String winDir = System.getenv("windir");
			p = Runtime.getRuntime().exec(winDir + "\\system32\\" + "tasklist.exe");
			BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			
			while((line = bfr.readLine()) != null) {
				String[] words = line.split(" ");
				String[] procInfo = new String[10];
				
				if(words[0].equals("ffmpeg.exe")) {
					int cnt = 0;
					
					for(String word : words) {
						if(word.equals("")) {
							continue;
						}
						procInfo[cnt] = word;
						cnt++;
					}
					pidList.add(procInfo[1]);
				}
			}
			
			bfr.close();
			bfr = null;
			if(pidList != null && pidList.size() > 0) {
				for(int j = 0; j < pidList.size(); j++) {
					String procPid = pidList.get(j);
					if(procPid.equals(pid)) {
						isExist = true;
						break;
					}
				}
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
			Runtime.getRuntime().exec("taskkill /F /PID " + pid);
			
			Thread.sleep(1000);
			
			String winDir = System.getenv("windir");
			p = Runtime.getRuntime().exec(winDir + "\\system32\\" + "tasklist.exe");
			BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			
			while((line = bfr.readLine()) != null) {
				String[] words = line.split(" ");
				String[] procInfo = new String[10];
				
				if(words[0].equals("ffmpeg.exe")) {
					int cnt = 0;
					for(String word : words) {
						if(word.equals("")) {
							continue;
						}
						procInfo[cnt] = word;
						cnt++;
					}
					pidList.add(procInfo[1]);
				}
			}
			bfr.close();
			bfr = null;
			
			boolean isKilled = true;
			
			for(int j = 0; j < pidList.size(); j++) {
				String procPid = pidList.get(j);
				if(procPid.equals(pid)) {
					isKilled = false;
					break;
				}
			}
			
			if(isKilled) {
				logger.info("[Kill "+pid+"] SUCCESS ");
			} else {
				logger.warn("[Kill "+pid+"] FAIL");
			}
			
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
