package com.kdy.live.dao.vod;

import java.util.Map;

import com.kdy.live.dto.live.LiveBroadcastVO;
import com.kdy.live.dto.vod.VodMetaVO;

public interface VodManageDAOFactoryIF {
	
	//MCMS LIVE Contents insert
	public void insertMcmsLiveVodData(LiveBroadcastVO lbvo) throws Exception;
	
	//Thumnail insert
	public void insertVodThumnail(Map<String, Object> map) throws Exception;
	
	//vod meta insert
	//ublic void insertVodMeta(VodMetaVO mvo) throws Exception;

	//TGLIVE_VOD Insert
	public void insertTgLiveVod(LiveBroadcastVO lbvo) throws Exception;
	
	//TGLIVE_VOD_FILE Insert
	public void insertVodMeta(VodMetaVO mvo) throws Exception;
}
