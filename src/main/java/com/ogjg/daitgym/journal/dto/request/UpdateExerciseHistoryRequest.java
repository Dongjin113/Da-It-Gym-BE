package com.ogjg.daitgym.journal.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class UpdateExerciseHistoryRequest {

    private int weight;
    private int count;
    private boolean completed;

    @Builder
    public UpdateExerciseHistoryRequest(int weight, int count, boolean completed) {
        this.weight = weight;
        this.count = count;
        this.completed = completed;
    }
}
