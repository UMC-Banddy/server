package com.umc.banddy.global.config;

import com.umc.banddy.domain.member.domain.Keyword;
import com.umc.banddy.domain.member.enums.KeywordCategory;
import com.umc.banddy.domain.member.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Order(120) // BandDummyDataLoader 다음 순서로 하고 싶으면 120 등 적절히 조정
public class KeywordDummyDataLoader implements CommandLineRunner {

    private final KeywordRepository keywordRepository;

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println(">>> KeywordDummyDataLoader START");

        // 한글 카테고리명 -> ENUM 매핑 (프로젝트 ENUM 이름에 맞춰 수정 가능)
        Map<String, KeywordCategory> catMap = new LinkedHashMap<>();
        catMap.put("매너", KeywordCategory.MANNER);
        catMap.put("스킬", KeywordCategory.SKILL);
        catMap.put("성향", KeywordCategory.STYLE);
        catMap.put("가능한 합주 주기", KeywordCategory.FREQ);

        // 카테고리별 키워드 목록
        Map<String, List<String>> seeds = Map.of(
                "매너", List.of("미리 조율해요", "약속 잘 키겨요", "연락이 빨라요", "연습 꼭 해와요", "일찍 가서 세팅해요", "호응 잘 해요"),
                "스킬", List.of("편곡 잘 해요", "코드 잘 따요"),
                "성향", List.of("자유로운 분위기 선호", "프로같은 분위기 선호"),
                "가능한 합주 주기", List.of("한 달에 한 번 이하", "격주", "주 2회", "주 3회", "매주", "불러주시는 대로 나가요")
        );

        int created = 0;
        for (var entry : seeds.entrySet()) {
            String koCategory = entry.getKey();
            KeywordCategory category = catMap.get(koCategory);
            if (category == null) {
                System.out.printf(">>> [WARN] 카테고리 매핑 없음: %s (건너뜀)%n", koCategory);
                continue;
            }
            for (String content : entry.getValue()) {
                // 중복 방지: content 유니크이므로 대소문자 무시로 검사
                if (keywordRepository.existsByContentIgnoreCase(content)) {
                    continue;
                }
                keywordRepository.save(
                        Keyword.builder()
                                .content(content)
                                .category(category)
                                .build()
                );
                created++;
            }
        }
        System.out.printf(">>> KeywordDummyDataLoader DONE. inserted=%d%n", created);
    }
}
