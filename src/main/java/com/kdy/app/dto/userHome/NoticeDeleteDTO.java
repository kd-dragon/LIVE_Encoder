package com.kdy.app.dto.userHome;

import java.util.List;

import lombok.Data;

@Data
public class NoticeDeleteDTO {
 private List<String> delNoticeSeqs;
 private String userId;
}
