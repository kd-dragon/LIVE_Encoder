package com.kdy.live.bean.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

public class RunConsoleRunnable implements Runnable
{
	private static final Logger logger = LoggerFactory.getLogger("ffmpeg");

	private InputStream _input;
	private String _name = "";
	private StringBuffer lines = null;
	private Process _process = null;
	private BufferedReader _stdout = null;
	private int _processExitValue = -1;
	
	public RunConsoleRunnable(List<String> cmdLine, String name)
	{
		_name = name;
		runConsole(cmdLine);
	}

	public RunConsoleRunnable(List<String> cmdLine, String curDir, String name)
	{
		_name = name;
		runConsole(cmdLine, curDir);
	}

	public RunConsoleRunnable(List<String> cmdLine, String curDir, String name, boolean result)
	{
		_name = name;
		if ( result ) {
			lines = new StringBuffer();
		}
		runConsole(cmdLine, curDir);
	}

	public Process getProcess() { return _process; }

	public BufferedReader getStdout() { return _stdout; }

	public InputStream getInputStream() { return _input; }

	public int getProcessExitValue() { return _processExitValue; }

	public String getResult() { return lines.toString(); }
	
	private void runConsole(List<String> cmdLine)
	{
		// 외부 프로세스 실행
		ProcessBuilder pb = new ProcessBuilder(cmdLine);
		Map env = pb.environment();
		
		pb.redirectErrorStream(true);
		
		try {
			_process = pb.start();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		_input = _process.getInputStream();
		_stdout = new BufferedReader( new InputStreamReader( _input ) );
	}

	private void runConsole(List<String> cmdLine, String curDir)
	{
		cmdLine.add( "-threads" );
		cmdLine.add( "0" );

		// 외부 프로세스 실행
		ProcessBuilder pb = new ProcessBuilder(cmdLine);
		
		if ( !curDir.isEmpty() ) {
			pb.directory( new File(curDir) );
		}
		pb.redirectErrorStream(true);
		try {
			_process = pb.start();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		_input = _process.getInputStream();
		_stdout = new BufferedReader( new InputStreamReader( _input ) );
	}

	//@Override
	public void run()
	{
		try {
			String line = null;
			while ( (line = _stdout.readLine()) != null ) {
				if ( lines != null ) {
					lines.append( line );
				}
	        }
			_stdout.close();
			_input.close();
		} catch ( Exception e ) {
			logger.error(e.getMessage());
		}
	}

	public int waitFor()
	{
		int exitValue = 0;
		
		try {
			exitValue = _process.waitFor();
			_processExitValue = exitValue;
			logger.info(_name + " - Process Exit Value: " + exitValue);
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		} finally {
			destroy();
		}
		
		return exitValue;
	}

	public void destroy()
	{
		_process.destroy();
		_process = null;
	}

	// Windows, Unix 환경에서 ffmpeg Process ID 구하기
	public long getProcessID() {
		long result = -1;
		Process p = _process;
				
		try {
			//for windows
		    if (p.getClass().getName().equals("java.lang.Win32Process") || p.getClass().getName().equals("java.lang.ProcessImpl")) 
		    {
		    	Field f = p.getClass().getDeclaredField("handle");
		    	f.setAccessible(true);              
		    	long handl = f.getLong(p);
		    	Kernel32 kernel = Kernel32.INSTANCE;
		    	WinNT.HANDLE hand = new WinNT.HANDLE();
		    	hand.setPointer(Pointer.createConstant(handl));
		    	result = kernel.GetProcessId(hand);
		    	f.setAccessible(false);
		    }
		            
		    //for unix based operating systems
		    else if (p.getClass().getName().equals("java.lang.UNIXProcess")) 
		    {
		    	Field f = p.getClass().getDeclaredField("pid");
		    	f.setAccessible(true);
		    	result = f.getLong(p);
		    	f.setAccessible(false);
		    }
		} catch(Exception ex) {
			ex.printStackTrace();
			result = -1;
		}
		        
		return result;
	}
}