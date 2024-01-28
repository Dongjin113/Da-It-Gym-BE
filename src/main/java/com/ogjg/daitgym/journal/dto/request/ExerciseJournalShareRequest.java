package com.ogjg.daitgym.journal.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class ExerciseJournalShareRequest {
    private boolean visible;
    private String split;

    public ExerciseJournalShareRequest(boolean visible, String split) {
        this.visible = visible;
        this.split = split;
    }
}
