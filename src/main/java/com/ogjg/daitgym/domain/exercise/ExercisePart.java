package com.ogjg.daitgym.domain.exercise;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class ExercisePart {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    private String part;

    public ExercisePart(Exercise exercise, String part) {
        this.exercise = exercise;
        this.part = part;
    }
}
