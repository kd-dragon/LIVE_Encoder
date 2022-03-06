package com.kdy.live.dao.live;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kdy.live.bean.util.OkHttpClientPool;
import com.kdy.live.dto.live.LiveBroadcastJobVO;
import com.kdy.live.dto.live.LiveBroadcastVO;
import com.kdy.live.dto.system.SystemConfigVO;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class LiveManageWEBDAO implements LiveManageDAOFactoryIF {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${spring.webUrl}")
	private String url;
	
	@Autowired
	private OkHttpClientPool pool;
	
	@Override
	public LiveBroadcastVO selectByStatusWait(LiveBroadcastVO lbvo) throws Exception {
		logger.info("LiveManageWEBDAO selectByStatusWait()");
		
		Gson gson = new Gson();
		OkHttpClient client = pool.getClient();
		
		Request request = new Request.Builder()
							.url(url + "/sched/live/selectByStatusWait.do")
							.post(RequestBody.create(gson.toJson(lbvo), MediaType.parse("application/json")))
							.build();
		Response response = client.newCall(request).execute();
		String resStr = response.body().string();
		
		LiveBroadcastVO rtnVo = new LiveBroadcastVO();
		
		try {
			rtnVo = gson.fromJson(resStr, new LiveBroadcastVO().getClass());
			//list = gson.fromJson(resStr, new TypeToken<List<VodManageVO>>() {}.getType());
			logger.info("==> selectByStatusWait : " + rtnVo.toString());
		} catch (Exception e) {
			logger.error("SELECT LIVE BROADCAST FAIL !!!\n" + e.getMessage());
		}
		
		return rtnVo;
	}
	
	@Override
	public List<LiveBroadcastVO> selectByStatusOnAir(LiveBroadcastVO lbvo) throws Exception {
		logger.info("LiveManageWEBDAO selectByStatusOnAir()");
		
		Gson gson = new Gson();
		OkHttpClient client = pool.getClient();
		
		Request request = new Request.Builder()
							.url(url + "/sched/live/selectByStatusOnAir.do")
							.post(RequestBody.create(gson.toJson(lbvo), MediaType.parse("application/json")))
							.build();
		Response response = client.newCall(request).execute();
		String resStr = response.body().string();
		
		List<LiveBroadcastVO> list = null;
		
		try {
			list = gson.fromJson(resStr, new TypeToken<List<LiveBroadcastVO>>() {}.getType());
			StringBuilder logMsg = new StringBuilder();
			logMsg.append("==> LiveManageWEBDAO > selectByStatusOnAir : \n");
			for(LiveBroadcastVO vo : list) {
				logMsg.append(vo.toString() + "\n");
			}
			logger.info(logMsg.toString());
		} catch (Exception e) {
			logger.error("SELECT LIVE BROADCAST FAIL !!!\n" + e.getMessage());
		}
		
		return list;
	}

	@Override
	public Boolean updateLiveBroadcast(LiveBroadcastVO lbvo) throws Exception {
		
		logger.info("LiveManageWEBDAO updateLiveBroadcast(" + lbvo.getLbSeq() +")");
		
		Gson gson = new Gson();
		
		OkHttpClient client = pool.getClient();
		
		Request request = new Request.Builder()
				.url(url+"/sched/live/updateLiveBroadcast.do")
				.post(RequestBody.create(gson.toJson(lbvo), MediaType.parse("application/json")))
				.build();
		
		Response response = client.newCall(request).execute();
		
		String str = response.body().toString();               
		Boolean flag = true;
		
		try {
			
			flag = gson.fromJson(str, Boolean.class);
			if (!flag) throw new Exception();
			
		} catch (Exception e) {
			logger.error("UPDATE SCHED_STATUS FAIL\n" + e.getMessage());
		}
		
		return flag;
		
	}

	@Override
	public String selectStatusBySeq(LiveBroadcastVO lbvo) throws Exception {
		logger.info("LiveManageWEBDAO selectStatusBySeq()");
		
		Gson gson = new Gson();
		OkHttpClient client = pool.getClient();
		
		Request request = new Request.Builder()
							.url(url + "/sched/live/selectStatusBySeq.do")
							.post(RequestBody.create(gson.toJson(lbvo), MediaType.parse("application/json")))
							.build();
		Response response = client.newCall(request).execute();
		String resStr = response.body().string();
		
		String status = null;
		
		try {
			status = gson.fromJson(resStr, new String().getClass());
			//list = gson.fromJson(resStr, new TypeToken<List<VodManageVO>>() {}.getType());
			logger.info("==> selectStatusBySeq : " + status);
		} catch (Exception e) {
			logger.error("SELECT LIVE STATUS FAIL !!!\n" + e.getMessage());
		}
		return status;
	}

	@Override
	public List<LiveBroadcastVO> selectByEndDate(String lbSerialNo) throws Exception {
		logger.info("LiveManageWEBDAO selectByEndDate()");
		
		Gson gson = new Gson();
		OkHttpClient client = pool.getClient();
		
		Request request = new Request.Builder()
							.url(url + "/sched/live/selectByEndDate.do")
							.post(RequestBody.create(lbSerialNo, MediaType.parse("application/json")))
							.build();
		Response response = client.newCall(request).execute();
		String resStr = response.body().string();
		
		List<LiveBroadcastVO> list = null;
		
		try {
			list = gson.fromJson(resStr, new TypeToken<List<LiveBroadcastVO>>() {}.getType());
			StringBuilder logMsg = new StringBuilder();
			logMsg.append("==> LiveManageWEBDAO > selectByEndDate : \n");
			for(LiveBroadcastVO vo : list) {
				logMsg.append(vo.toString() + "\n");
			}
			logger.info(logMsg.toString());
		} catch (Exception e) {
			logger.error("SELECT END DATE LIVE BROADCAST FAIL !!!\n" + e.getMessage());
		}
		
		return list;
	}

	@Override
	public Boolean updateLiveBroadcastJob(LiveBroadcastVO lbvo) throws Exception {
		logger.info("LiveManageWEBDAO updateLiveBroadcastJob(" + lbvo.getLbSeq() +")");
		
		Gson gson = new Gson();
		
		OkHttpClient client = pool.getClient();
		
		Request request = new Request.Builder()
				.url(url+"/sched/live/updateLiveBroadcastJob.do")
				.post(RequestBody.create(gson.toJson(lbvo), MediaType.parse("application/json")))
				.build();
		
		Response response = client.newCall(request).execute();
		
		String str = response.body().toString();               
		Boolean flag = true;
		
		try {
			
			flag = gson.fromJson(str, Boolean.class);
			if (!flag) throw new Exception();
			
		} catch (Exception e) {
			logger.error("UPDATE BROADCAST JOB FAIL\n" + e.getMessage());
		}
		
		return flag;
	}

	@Override
	public Boolean jobLogMove(LiveBroadcastVO lbvo) throws Exception {
		logger.info("LiveManageWEBDAO jobLogMove(" + lbvo.getLbSeq() +")");
		
		Gson gson = new Gson();
		
		OkHttpClient client = pool.getClient();
		
		Request request = new Request.Builder()
				.url(url+"/sched/live/jobLogMove.do")
				.post(RequestBody.create(gson.toJson(lbvo), MediaType.parse("application/json")))
				.build();
		
		Response response = client.newCall(request).execute();
		
		String str = response.body().toString();               
		Boolean flag = true;
		
		try {
			
			flag = gson.fromJson(str, Boolean.class);
			if (!flag) throw new Exception();
			
		} catch (Exception e) {
			logger.error("INSERT BROADCAST JOB LOG FAIL\n" + e.getMessage());
		}
		
		return flag;
	}

	@Override
	public Boolean jobDataDelete(LiveBroadcastVO lbvo) throws Exception {
		logger.info("LiveManageWEBDAO jobDataDelete(" + lbvo.getLbSeq() +")");
		
		Gson gson = new Gson();
		
		OkHttpClient client = pool.getClient();
		
		Request request = new Request.Builder()
				.url(url+"/sched/live/jobDataDelete.do")
				.post(RequestBody.create(gson.toJson(lbvo), MediaType.parse("application/json")))
				.build();
		
		Response response = client.newCall(request).execute();
		
		String str = response.body().toString();               
		Boolean flag = true;
		
		try {
			
			flag = gson.fromJson(str, Boolean.class);
			if (!flag) throw new Exception();
			
		} catch (Exception e) {
			logger.error("DELETE BROADCAST JOB FAIL\n" + e.getMessage());
		}
		
		return flag;
	}

	@Override
	public SystemConfigVO selectSystemConfig() throws Exception {
		logger.info("LiveManageWEBDAO selectStatusBySeq()");
		
		Gson gson = new Gson();
		OkHttpClient client = pool.getClient();
		
		Request request = new Request.Builder()
							.url(url + "/sched/live/selectSystemConfig.do")
							.post(RequestBody.create("", MediaType.parse("application/json")))
							.build();
		Response response = client.newCall(request).execute();
		String resStr = response.body().string();
		
		SystemConfigVO svo = null;
		
		try {
			svo = gson.fromJson(resStr, SystemConfigVO.class);
			//list = gson.fromJson(resStr, new TypeToken<List<VodManageVO>>() {}.getType());
			logger.info("==> select System Config VO : " + svo.toString());
		} catch (Exception e) {
			logger.error("SELECT SYSTEM CONFIG FAIL !!!\n" + e.getMessage());
		}
		return svo;
	}

	@Override
	public LiveBroadcastJobVO selectBroadcastJob(LiveBroadcastVO lbvo) throws Exception {
		logger.info("LiveManageWEBDAO selectBroadcastJob()");
		
		Gson gson = new Gson();
		OkHttpClient client = pool.getClient();
		
		Request request = new Request.Builder()
							.url(url + "/sched/live/selectBroadcastJob.do")
							.post(RequestBody.create("", MediaType.parse("application/json")))
							.build();
		Response response = client.newCall(request).execute();
		String resStr = response.body().string();
		
		LiveBroadcastJobVO lbjvo = null;
		
		try {
			lbjvo = gson.fromJson(resStr, LiveBroadcastJobVO.class);
			//list = gson.fromJson(resStr, new TypeToken<List<VodManageVO>>() {}.getType());
			logger.info("==> select LiveBroadcastJob VO : " + lbjvo.toString());
		} catch (Exception e) {
			logger.error("SELECT LiveBroadcastJob FAIL !!!\n" + e.getMessage());
		}
		return lbjvo;
	}

	@Override
	public Boolean updateLiveSerialNo(LiveBroadcastVO lbvo) throws Exception {
		logger.info("LiveManageWEBDAO updateLiveSerialNo(" + lbvo.getLbSeq() +")");
		
		Gson gson = new Gson();
		
		OkHttpClient client = pool.getClient();
		
		Request request = new Request.Builder()
				.url(url+"/sched/live/updateLiveSerialNo.do")
				.post(RequestBody.create(gson.toJson(lbvo), MediaType.parse("application/json")))
				.build();
		
		Response response = client.newCall(request).execute();
		
		String str = response.body().toString();               
		Boolean flag = true;
		
		try {
			
			flag = gson.fromJson(str, Boolean.class);
			if (!flag) throw new Exception();
			
		} catch (Exception e) {
			logger.error("UPDATE LIVE SERAIL NO FAIL\n" + e.getMessage());
		}
		
		return flag;
	}

	@Override
	public String selectNowDuration(String lbSeq) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean updateLiveEndDate(String lbSeq) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LiveBroadcastVO> selectByInterruptedBroadcast(String lbSerialNo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LiveBroadcastVO> selectByInterruptedOthers(String lbSerialNo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LiveBroadcastVO> selectByStopStatusOnAir(LiveBroadcastVO lbvo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean selectBroadcastEnable(String mySerialNo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String selectDelYnBySeq(LiveBroadcastVO lbvo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
