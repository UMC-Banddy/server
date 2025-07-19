//package com.umc.banddy.domain.chat.domain.Recruitment;
//
//import com.umc.banddy.domain.chat.domain.enums.SessionStatus;
//import com.umc.banddy.domain.member.domain.Session;
//import com.umc.banddy.global.entity.BaseEntity;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.Setter;
//
//@Entity
//@Getter
//@AllArgsConstructor
//@Builder
//@Setter
//public class RecruitmentSession extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Enumerated(value = EnumType.STRING)
//    private SessionStatus status;
//
//    @ManyToOne
//    @JoinColumn(name = "Session_id", nullable = false)
//    private Session session;
//
//    @ManyToOne
//    @JoinColumn(name = "recruitment_room_id", nullable = false)
//    private RecruitmentRoom recruitmentRoom;
//
//}
