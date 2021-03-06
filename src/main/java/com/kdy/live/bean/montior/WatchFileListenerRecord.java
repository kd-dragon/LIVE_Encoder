package com.kdy.live.bean.montior;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.LiveSchedMemoryVO.RedisHashKeyword;
import com.kdy.live.dto.live.LiveBroadcastVO;

public class WatchFileListenerRecord implements FileAlterationListener {
	
	private Logger logger = LoggerFactory.getLogger("ffmpeg");
	
	private final LiveBroadcastVO lbvo;
	private final LiveSchedMemoryVO mvo;
	private final RedisTemplate<String, Object> template;
	private final RedisTemplate<String, Object> recordTemplate;
	
	public WatchFileListenerRecord(	  LiveSchedMemoryVO mvo
									, LiveBroadcastVO lbvo
									, RedisTemplate<String, Object> template
									, RedisTemplate<String, Object> recordTemplate
	) {
		this.mvo = mvo;
		this.lbvo = lbvo;
		this.template = template;
		this.recordTemplate = recordTemplate;
	}
	
	@Override
	public void onDirectoryCreate(File directory) {
		logger.info("########### onDirectoryCreate : " + directory.getName());
	}

	@Override
	public void onDirectoryChange(File directory) {
		logger.info("########### onDirectoryChange : " + directory.getName());
	}

	@Override
	public void onDirectoryDelete(File directory) {
		logger.info("########### onDirectoryDelete : " + directory.getName());
	}

	@Override
	public void onFileCreate(File file) {
		String key = RedisHashKeyword.RECORD.toString() + lbvo.getLbSeq() + lbvo.getLbSerialNo();
		logger.info("########### onFileCreate >> key :" + key);
		String fileName = file.getName();
		
		// TS파일만 녹화여부 확인 후에 REDIS 내 녹화 리스트에 추가 
		if(fileName.toLowerCase().contains(".ts")) {
			Object recEnable = recordTemplate.opsForValue().get(RedisHashKeyword.RECENABLE.toString() + lbvo.getLbSeq());
			logger.info("{} >> {}", RedisHashKeyword.RECENABLE.toString(), recEnable.toString()); 
			if(recEnable != null && recEnable.toString().equalsIgnoreCase("Y")) {
				ListOperations<String, Object> listOperation = recordTemplate.opsForList();
				listOperation.rightPush(key, file.getName());
				recordTemplate.expire(key, 30, TimeUnit.DAYS);
			}
		}
	}

	@Override
	public void onFileChange(File file) {
		String key = lbvo.getLbSeq() + "_" + file.getName();
		logger.info("########### onFileChange >> key :" + key);
		
		assignFileToMemoryReplication(key, file);
	}

	@Override
	public void onFileDelete(File file) {
		logger.info("########### onFileDelete : " + file.getName());
	}
	
	/**
	 *  start, stop은 매번 반복되는 함수
	 */
	@Override
	public void onStart(FileAlterationObserver observer) {
	}
	
	@Override
	public void onStop(FileAlterationObserver observer) {
	}
	
	private byte[] convertFileToByteArray(File file) {
		byte[] fileBytes = null;
		
		try {
			fileBytes = FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return fileBytes;
	}
	
	/**
	 * Master 1 Slave N 구성 -> Replication 방식
	 * @param key
	 * @param file
	 */
	private void assignFileToMemoryReplication(String key, File file) {
		ValueOperations<String, Object> valueOperations = template.opsForValue();
//		logger.info("check monitor : " + key + "/" + file);
		valueOperations.set(key, convertFileToByteArray(file), 60, TimeUnit.SECONDS);
	}
	
	/**
	 * Master N 개 구성 -> Pub-Sub 방식
	 * @param key
	 * @param file
	 */
	/*
	private void assignFileToMemoryPubsub(String key, File file) {
		
		
		StreamPushVO pushVo = new StreamPushVO();
		pushVo.setKey(key);
		pushVo.setStreamData(convertFileToByteArray(file));
		
		pubSubTemplate.convertAndSend(lbvo.getTopic().getTopic(), pushVo);
		
		// topic listener 등록 전에 요청 대비
		assignFileToMemoryReplication(key, file);
	}
	*/
}
