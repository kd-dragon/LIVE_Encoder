package com.kdy.live.bean.montior;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.util.code.StreamType;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.LiveSchedMemoryVO.RedisHashKeyword;
import com.kdy.live.dto.live.LiveBroadcastVO;

@Component
public class WatchFileHandler {
	
	private Logger logger = LoggerFactory.getLogger("ffmpeg");
	
	private final RedisTemplate<String, Object> template;
	
	private final RedisTemplate<String, Object> recordTemplate;
	
	private final LiveSchedMemoryVO memoryVO;
	
	@Autowired
	public WatchFileHandler(LiveSchedMemoryVO memoryVO
			, @Qualifier("redisTemplate") RedisTemplate<String, Object> template
			, @Qualifier("redisTemplateObject") RedisTemplate<String, Object> recordTemplate) {
		this.memoryVO = memoryVO;
		this.template = template;
		this.recordTemplate = recordTemplate;
	}
	
	//적응형 스트리밍 적용 X
	public void monitoring(LiveBroadcastVO lbvo) {
		logger.info("Start Monitoring >> " + lbvo.getLbTitle());
		
		File directory = new File(lbvo.getHlsFilePath());
		
		logger.info("directory info >> " + lbvo.getHlsFilePath());
		
		//file filter
		IOFileFilter m3u8_file = FileFilterUtils.and(FileFilterUtils.fileFileFilter()
									, FileFilterUtils.suffixFileFilter(".m3u8", IOCase.INSENSITIVE));
		IOFileFilter ts_files = FileFilterUtils.and(FileFilterUtils.fileFileFilter()
									, FileFilterUtils.suffixFileFilter(".ts", IOCase.INSENSITIVE));

		IOFileFilter filter = FileFilterUtils.or(m3u8_file, ts_files);
		
		FileAlterationObserver observer = new FileAlterationObserver(directory, filter);
		FileAlterationMonitor monitor = new FileAlterationMonitor(1000L); //1초
		
		if(lbvo.getLbVodSaveYn() != null && lbvo.getLbVodSaveYn().equalsIgnoreCase("Y")) {
			
			ValueOperations<String, Object> valueOperation = recordTemplate.opsForValue();
			String key = RedisHashKeyword.RECENABLE.toString() + lbvo.getLbSeq();
			if(valueOperation.get(key) == null) {
				valueOperation.set(key, "N", 3, TimeUnit.DAYS);
			}
			
			if(memoryVO.getChatRecYn().equalsIgnoreCase("Y")){
				//채팅 사용할 경우
				if(lbvo.getLbChatYn().equalsIgnoreCase("Y")) {
					String key2 = RedisHashKeyword.RECSTARTDATE.toString() + lbvo.getLbSeq();
					String key3 = RedisHashKeyword.RECDURATION.toString() + lbvo.getLbSeq();
					
					if(valueOperation.get(key2) == null) {
						valueOperation.set(key2, 0, 3, TimeUnit.DAYS);
					}
					
					if(valueOperation.get(key3) == null) {
						valueOperation.set(key3, 0, 3, TimeUnit.DAYS);
					}
				}
			}
			
			observer.addListener(new WatchFileListenerRecord(memoryVO, lbvo, template, recordTemplate));
			
		} else {
			observer.addListener(new WatchFileListener(memoryVO, lbvo, template));
		}
		
		monitor.addObserver(observer);
		
		try {
			monitor.start();
			lbvo.setWatchMonitor(monitor);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			
			try {
				monitor.stop();
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				lbvo.setWatchMonitor(null);
			}
		} 
	}
	
	//적응형 스트리밍 적용 O
	public void adaptiveMonitoring(LiveBroadcastVO lbvo) {
		logger.info("Start adaptiveMonitoring >> " + lbvo.getLbTitle());
		
		//adaptive >> basic
		File highDirectory = new File(lbvo.getHlsFilePath()+ StreamType.high.ordinal());
		File lowDirectory = new File(lbvo.getHlsFilePath()+ StreamType.low.ordinal());
		File midDirectory = null;
		
		//adaptive >> advance
		if(memoryVO.getEncodingType().equals("advance")) {
			midDirectory = new File(lbvo.getHlsFilePath() + StreamType.mid.ordinal());
		}
		
		logger.info("adaptiveDirectory info >> " + lbvo.getHlsFilePath());
		
		//file filter
		IOFileFilter m3u8_file = FileFilterUtils.and(FileFilterUtils.fileFileFilter()
				, FileFilterUtils.suffixFileFilter(".m3u8", IOCase.INSENSITIVE));
		IOFileFilter ts_files = FileFilterUtils.and(FileFilterUtils.fileFileFilter()
				, FileFilterUtils.suffixFileFilter(".ts", IOCase.INSENSITIVE));
		
		IOFileFilter filter = FileFilterUtils.or(m3u8_file, ts_files);
		
		FileAlterationObserver highObserver = new FileAlterationObserver(highDirectory, filter);
		FileAlterationObserver lowObserver = new FileAlterationObserver(lowDirectory, filter);
		
		FileAlterationMonitor monitor = new FileAlterationMonitor(1000L); //1초
		
		highObserver.addListener(new AdaptiveWatchFileListener(memoryVO, lbvo, template));
		lowObserver.addListener(new AdaptiveWatchFileListener(memoryVO, lbvo, template));
		
		monitor.addObserver(highObserver);
		monitor.addObserver(lowObserver);
		
		//adaptive >> advance : basic(high, low) + mid
		if(memoryVO.getEncodingType().equals("advance")) {
			FileAlterationObserver midObserver = new FileAlterationObserver(midDirectory, filter);
			midObserver.addListener(new AdaptiveWatchFileListener(memoryVO, lbvo, template));
			monitor.addObserver(midObserver);
		}
		
		try {
			monitor.start();
			lbvo.setWatchMonitor(monitor);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			
			try {
				monitor.stop();
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				lbvo.setWatchMonitor(null);
			}
		} 
	}
}
