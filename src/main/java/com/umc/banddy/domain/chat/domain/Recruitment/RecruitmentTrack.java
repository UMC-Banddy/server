//package com.umc.banddy.domain.chat.domain.Recruitment;
//
//import com.umc.banddy.domain.music.album.domain.Album;
//import com.umc.banddy.domain.music.track.domain.Track;
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
//public class RecruitmentTrack {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "track_id", nullable = false)
//    private Track track;
//
//    @ManyToOne
//    @JoinColumn(name = "recruitment_room_id", nullable = false)
//    private RecruitmentRoom recruitmentRoom;
//
//}
