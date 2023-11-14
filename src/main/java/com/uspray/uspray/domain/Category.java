package com.uspray.uspray.domain;

import com.uspray.uspray.DTO.category.CategoryRequestDto;
import com.uspray.uspray.common.domain.AuditingTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE category SET deleted = true WHERE category_id = ?")
@Where(clause = "deleted=false")
public class Category extends AuditingTimeEntity {

    private final Boolean deleted = false;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    @Id
    private Long id;
    private String name;
    private String color;
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "category_order")
    private int order;
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Category(
        Long id,
        String name,
        String color,
        int order,
        Member member
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.order = order;
        this.member = member;
    }

    public void update(CategoryRequestDto categoryRequestDto) {
        this.name = categoryRequestDto.getName();
        this.color = categoryRequestDto.getColor();
    }
}
