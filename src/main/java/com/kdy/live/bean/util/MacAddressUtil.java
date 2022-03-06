package com.kdy.live.bean.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacAddressUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(MacAddressUtil.class);
	
	public static String getLocalMacAddress() {
	 	String result = "";
		InetAddress ip;

		try {
			ip = InetAddress.getLocalHost();
		   
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
		   
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			
			result = sb.toString();
			
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (SocketException e){
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String getShortMacAddress() {  
        String value = "";  
  
        try {  
            value = getMacAddress();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        value = value.replaceAll("-", "");  
  
        return value;  
    }
	
	public final static String getMacAddress() throws IOException {  
        String os = System.getProperty("os.name");  
  
        if (os.startsWith("Windows")) {  
            return ParseMacAddress(windowsRunIpConfigCommand());  
        } else if (os.startsWith("Linux")) {  
            return ParseMacAddress(linuxRunIfConfigCommand());  
        } else {  
            throw new IOException("unknown operating system: " + os);  
        }  
  
    }
	
	private final static String linuxRunIfConfigCommand() throws IOException {  
        Process p = Runtime.getRuntime().exec("ifconfig");  
        InputStream stdoutStream = new BufferedInputStream(p.getInputStream());  
  
        StringBuffer buffer = new StringBuffer();  
        for (;;) {  
            int c = stdoutStream.read();  
            if (c == -1)  
                break;  
            buffer.append((char) c);  
        }  
        String outputText = buffer.toString();  
        stdoutStream.close();  
  
        return outputText;  
    }
	
	private final static String windowsRunIpConfigCommand() throws IOException {  
        Process p = Runtime.getRuntime().exec("ipconfig /all");  
        InputStream stdoutStream = new BufferedInputStream(p.getInputStream());  
  
        StringBuffer buffer = new StringBuffer();  
        for (;;) {  
            int c = stdoutStream.read();  
            if (c == -1)  
                break;  
            buffer.append((char) c);  
        }  
        String outputText = buffer.toString();  
  
        stdoutStream.close();  
  
        return outputText;  
    }
	 
	public static String ParseMacAddress(String text) {  
        String result = null;  
        String[] list = text.split("\\p{Alnum}{2}(-\\p{Alnum}{2}){5}");  
        int index = 0;  
        for (String str : list) {  
            if (str.length() < text.length()) {  
                index = str.length();  
                result = text.substring(index, index + 17);  
                if (!result.equals("00-00-00-00-00-00")) {  
                    break;
                }  
                text = text.substring(index + 17);  
            }  
        }
        return result;  
	} 
	
}
