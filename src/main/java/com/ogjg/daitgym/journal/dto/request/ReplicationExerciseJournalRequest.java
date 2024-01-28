package com.ogjg.daitgym.journal.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class ReplicationExerciseJournalRequest {

    private LocalDate journalDate;

    public ReplicationExerciseJournalRequest(LocalDate journalDate) {
        this.journalDate = journalDate;
    }
}
