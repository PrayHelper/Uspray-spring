package com.uspray.uspray.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Notification {

    private String title;
    private String body;
    private String image;
    @Id
    private Long id;

    @Builder
    public Notification(String title, String body, String image, Long id) {
        this.title = title;
        this.body = body;
        this.image = image;
        this.id = id;
    }
}
