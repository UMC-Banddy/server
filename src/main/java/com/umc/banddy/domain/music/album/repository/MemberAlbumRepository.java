package com.umc.banddy.domain.music.album.repository;

import com.umc.banddy.domain.member.Member;
import com.umc.banddy.domain.music.album.domain.Album;
import com.umc.banddy.domain.music.album.domain.MemberAlbum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberAlbumRepository extends JpaRepository<MemberAlbum, Long> {
    Optional<MemberAlbum> findByMemberAndAlbum(Member member, Album album);
    List<MemberAlbum> findAllByMember(Member member);
    void deleteByMemberAndAlbum(Member member, Album album);
}
