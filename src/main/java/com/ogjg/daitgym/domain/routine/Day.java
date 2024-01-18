package com.ogjg.daitgym.domain.routine;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "days")
public class Day {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @OneToMany(mappedBy = "day", fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseDetail> exerciseDetails = new ArrayList<>();

    private int dayNumber;

    @Builder
    public Day(Routine routine, int dayNumber) {
        this.routine = routine;
        this.dayNumber = dayNumber;
    }

    public void addExerciseDetail(ExerciseDetail exerciseDetail) {
        this.exerciseDetails.add(exerciseDetail);
        exerciseDetail.setDay(this);
    }
}
