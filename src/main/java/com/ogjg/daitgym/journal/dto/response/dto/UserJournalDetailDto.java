package com.ogjg.daitgym.journal.dto.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ogjg.daitgym.domain.journal.ExerciseJournal;
import com.ogjg.daitgym.domain.TimeTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class UserJournalDetailDto {

    private Long id;
    private final int order = 1;
    @JsonProperty("spread")
    private final boolean isSpread = false;
    private LocalDate dayDate;
    @JsonProperty("completed")
    private boolean isCompleted;
    private TimeTemplate exerciseTime;
    private List<UserJournalDetailExerciseListDto> exercises = new ArrayList<>();

    public UserJournalDetailDto(
            ExerciseJournal exerciseJournal,
            List<UserJournalDetailExerciseListDto> exercises
    ) {
        this.id = exerciseJournal.getId();
        this.dayDate = exerciseJournal.getJournalDate();
        this.isCompleted = exerciseJournal.isCompleted();
        this.exerciseTime = exerciseJournal.getExerciseTime();
        this.exercises = exercises;

    }
}
