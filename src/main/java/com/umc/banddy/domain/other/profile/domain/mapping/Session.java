package com.umc.banddy.domain.other.profile.domain.mapping;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Session {
    @Id
    private Long id;

    private String name;
    private String icon;

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }
}

