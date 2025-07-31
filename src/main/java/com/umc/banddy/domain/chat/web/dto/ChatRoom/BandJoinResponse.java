 package com.umc.banddy.domain.chat.web.dto.ChatRoom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

 @Getter
 @AllArgsConstructor
 @Builder
 public class BandJoinResponse {

     private Long roomId;

     private String bandName;

     private String bandProfileUrl;

     private Long managerId;

     private String managerName;

     private String managerProfileUrl;
 }
