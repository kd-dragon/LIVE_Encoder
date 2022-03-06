package com.kdy.live.bean.util.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LiveBroadcastStatus implements EnumMapperType {
	
	Wait("0"), OnAir("1"), Finished("2"), Pause("3"), Restart("4"), Recording("5"), Start("8"), Error("9");
	
	@Getter
	private String title;
	
	@Override
	public String getCode() {
		return name();
	}

}
