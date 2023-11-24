package com.uspray.uspray.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Notification {

    private String title;
    private String body;
    private String image;
    @Id
    private Long id;
}
