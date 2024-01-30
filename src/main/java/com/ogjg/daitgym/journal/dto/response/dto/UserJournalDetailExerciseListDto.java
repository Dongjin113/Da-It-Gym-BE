package com.ogjg.daitgym.journal.dto.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ogjg.daitgym.domain.TimeTemplate;
import com.ogjg.daitgym.domain.journal.ExerciseList;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class UserJournalDetailExerciseListDto {

    private Long id;
    @JsonProperty("spread")
    private final boolean isSpread = false;
    private String name;
    private String part;
    private int order;
    private TimeTemplate restTime;
    private List<UserJournalDetailExerciseHistoryDto> exerciseSets = new ArrayList<>();

    public UserJournalDetailExerciseListDto(
            ExerciseList exerciseList, String part,
            List<UserJournalDetailExerciseHistoryDto> exerciseSets
    ) {
        this.id = exerciseList.getId();
        this.name = exerciseList.getExercise().getName();
        this.part = part;
        this.order = exerciseList.getExerciseNum();
        this.restTime = exerciseList.getRestTime();
        this.exerciseSets = exerciseSets;
    }
}
