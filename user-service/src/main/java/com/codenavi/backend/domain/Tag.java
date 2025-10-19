package com.codenavi.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
public class Tag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // 영문 이름, 태그용

    @Column(name = "display_name")
    private String displayName; // 표시용 이름

    @Lob
    @Column(name = "tags_description", columnDefinition = "TEXT")
    private String description; // 설명

    private String color; // 색상코드

//    @Column(name = "usage_count")
//    private Integer usageCount;

    @Column(name = "is_active")
    private boolean isActive;

    // Relationships
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProblemTag> problemTags = new ArrayList<>();
}
