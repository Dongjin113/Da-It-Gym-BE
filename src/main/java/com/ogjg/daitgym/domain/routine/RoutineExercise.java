package com.ogjg.daitgym.domain.routine;

import com.ogjg.daitgym.domain.BaseEntity;
import com.ogjg.daitgym.domain.exercise.Exercise;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class RoutineExercise extends BaseEntity {

    @EmbeddedId
    private RoutineExercisePK routineExercisePK;

    @MapsId("routineId")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @MapsId("exerciseId")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Column(name = "days")
    private int day;

    private int setCount;

    private int repetitionCount;

    private int weight;

    @Builder
    public RoutineExercise(RoutineExercisePK routineExercisePK, int day, int setCount, int repetitionCount, int weight) {
        this.routineExercisePK = routineExercisePK;
        this.day = day;
        this.setCount = setCount;
        this.repetitionCount = repetitionCount;
        this.weight = weight;
    }

    @Getter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class RoutineExercisePK implements Serializable {

        private Long routineId;
        private Long exerciseId;

        public RoutineExercisePK(Long routineId, Long exerciseId) {
            this.routineId = routineId;
            this.exerciseId = exerciseId;
        }
    }
}
