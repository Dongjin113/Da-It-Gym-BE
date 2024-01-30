package com.ogjg.daitgym.journal.dto.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ogjg.daitgym.domain.journal.ExerciseHistory;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class UserJournalDetailExerciseHistoryDto {

    private Long id;
    private int order;
    private int weights;
    private int counts;
    @JsonProperty("completed")
    private boolean isCompleted;

    public UserJournalDetailExerciseHistoryDto(ExerciseHistory exerciseHistory) {
        this.id = exerciseHistory.getId();
        this.order = exerciseHistory.getSetNum();
        this.weights = exerciseHistory.getWeight();
        this.counts = exerciseHistory.getRepetitionCount();
        this.isCompleted = exerciseHistory.isCompleted();
    }
}
