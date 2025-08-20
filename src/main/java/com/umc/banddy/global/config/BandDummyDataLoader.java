package com.umc.banddy.global.config;

import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.enums.BandStatus;
import com.umc.banddy.domain.band.profile.enums.Gender;
import com.umc.banddy.domain.band.profile.repository.BandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Order(100)
public class BandDummyDataLoader implements CommandLineRunner {

    private final BandRepository bandRepository;

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println(">>> BandDummyDataLoader START");
        if (bandRepository.count() > 0) {
            System.out.println(">>> exists, skip");
            return;
        }
        IntStream.rangeClosed(1, 10).forEach(i -> {
            Band b = Band.builder()
                    .name("밴드 " + i)
                    .description("밴드 " + i + " 설명")
                    .profileImageUrl("https://example.com/band" + i + ".png")
                    .representativeSong("대표곡 " + i)
                    .endDate(LocalDateTime.now().plusDays(30))
                    .autoClose(true)
                    .ageStart(20).ageEnd(30)
                    .gender(Gender.OTHER)
                    .region("서울")
                    .district(i % 2 == 0 ? "강남구" : "마포구")
                    .status(BandStatus.RECRUITING)
                    .averageAge("25")
                    .maleCount(3).femaleCount(2)
                    .build();
            bandRepository.save(b);
        });
        System.out.println(">>> BandDummyDataLoader DONE");
    }
}
