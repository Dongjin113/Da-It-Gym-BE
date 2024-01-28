package com.ogjg.daitgym.domain.feed;

import com.ogjg.daitgym.domain.BaseEntity;
import com.ogjg.daitgym.domain.journal.ExerciseJournal;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class FeedExerciseJournal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "feed_journal_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "journal_id")
    private ExerciseJournal exerciseJournal;

    @OneToMany(mappedBy = "feedExerciseJournal")
    private List<FeedExerciseJournalImage> feedExerciseJournalImages = new ArrayList<>();

    public FeedExerciseJournal(ExerciseJournal exerciseJournal) {
        this.exerciseJournal = exerciseJournal;
    }

    @Builder
    public FeedExerciseJournal(
            ExerciseJournal exerciseJournal,
            List<FeedExerciseJournalImage> feedExerciseJournalImages
    ) {
        this.exerciseJournal = exerciseJournal;
        this.feedExerciseJournalImages = feedExerciseJournalImages;
    }
}
