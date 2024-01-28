package com.ogjg.daitgym.journal.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class ExerciseHistoryRequest {

    private Long id;
    private int setNum;
    private int weights;
    private int counts;

    @Builder
    public ExerciseHistoryRequest(Long id, int setNum, int weights, int counts) {
        this.id = id;
        this.setNum = setNum;
        this.weights = weights;
        this.counts = counts;
    }
}
