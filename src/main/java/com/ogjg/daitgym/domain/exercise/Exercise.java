package com.ogjg.daitgym.domain.exercise;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Exercise {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "exercise")
    @JoinColumn(name = "exercise_part_id")
    private ExercisePart exercisePart;

    @Column(unique = true)
    private String name;

    public Exercise(String name) {
        this.name = name;
    }
}
