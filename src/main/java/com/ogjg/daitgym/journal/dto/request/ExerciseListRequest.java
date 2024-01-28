package com.ogjg.daitgym.journal.dto.request;

import com.ogjg.daitgym.domain.TimeTemplate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class ExerciseListRequest {

    private Long id;
    private String name;
    private int exerciseNum;
    private TimeTemplate restTime;
    List<ExerciseHistoryRequest> exerciseSets = new ArrayList<>();

    @Builder
    public ExerciseListRequest(Long id, String name, int exerciseNum, TimeTemplate restTime, List<ExerciseHistoryRequest> exerciseSets) {
        this.id = id;
        this.name = name;
        this.exerciseNum = exerciseNum;
        this.restTime = restTime;
        this.exerciseSets = exerciseSets;
    }
}
