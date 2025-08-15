 package com.umc.banddy.domain.chat.web.dto.chatroom.creation;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

 @Getter
 @AllArgsConstructor
 @Builder
 public class BandJoinResponse {

     @Positive
     private Long roomId;

     private String bandName;

     private String bandProfileUrl;
     @Positive
     private Long managerId;

     private String managerName;

     private String managerProfileUrl;
 }
