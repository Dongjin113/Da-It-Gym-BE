package com.ogjg.daitgym.journal.dto.request;

import com.ogjg.daitgym.domain.TimeTemplate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class ExerciseJournalCompleteRequest {

    private boolean completed;
    private TimeTemplate exerciseTime;

    public ExerciseJournalCompleteRequest(boolean completed, TimeTemplate exerciseTime) {
        this.completed = completed;
        this.exerciseTime = exerciseTime;
    }
}
