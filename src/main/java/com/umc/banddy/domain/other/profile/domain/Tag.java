package com.umc.banddy.domain.other.profile.domain;

import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Tag extends BaseEntity {
    @Id
    private Long id;

    private String name;
}

