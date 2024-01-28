package com.ogjg.daitgym.journal.service;

import com.ogjg.daitgym.common.exception.ErrorCode;
import com.ogjg.daitgym.common.exception.feed.AlreadyExistFeedJournal;
import com.ogjg.daitgym.common.exception.journal.NotCompletedExerciseHistory;
import com.ogjg.daitgym.domain.TimeTemplate;
import com.ogjg.daitgym.domain.User;
import com.ogjg.daitgym.domain.exercise.Exercise;
import com.ogjg.daitgym.domain.exercise.ExercisePart;
import com.ogjg.daitgym.domain.feed.FeedExerciseJournal;
import com.ogjg.daitgym.domain.feed.FeedExerciseJournalImage;
import com.ogjg.daitgym.domain.journal.ExerciseHistory;
import com.ogjg.daitgym.domain.journal.ExerciseJournal;
import com.ogjg.daitgym.domain.journal.ExerciseList;
import com.ogjg.daitgym.exercise.repository.ExercisePartRepository;
import com.ogjg.daitgym.exercise.repository.ExerciseRepository;
import com.ogjg.daitgym.exercise.service.ExerciseHelper;
import com.ogjg.daitgym.feed.repository.FeedExerciseJournalImageRepository;
import com.ogjg.daitgym.feed.repository.FeedExerciseJournalRepository;
import com.ogjg.daitgym.feed.service.FeedJournalHelper;
import com.ogjg.daitgym.journal.dto.request.*;
import com.ogjg.daitgym.journal.dto.response.UserJournalDetailResponse;
import com.ogjg.daitgym.journal.dto.response.UserJournalListResponse;
import com.ogjg.daitgym.journal.repository.exercisehistory.ExerciseHistoryRepository;
import com.ogjg.daitgym.journal.repository.exerciselist.ExerciseListRepository;
import com.ogjg.daitgym.journal.repository.journal.ExerciseJournalReplicationHistoryRepository;
import com.ogjg.daitgym.journal.repository.journal.ExerciseJournalRepository;
import com.ogjg.daitgym.routine.repository.RoutineRepository;
import com.ogjg.daitgym.s3.repository.S3Repository;
import com.ogjg.daitgym.user.repository.UserRepository;
import com.ogjg.daitgym.user.service.UserHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
@Transactional
class ExerciseJournalServiceTest {

    @Autowired
    @InjectMocks
    ExerciseJournalService exerciseJournalService;

    @Autowired
    FeedJournalHelper feedJournalHelper;

    @Autowired
    S3Repository s3Repository;

    @Autowired
    S3Client s3Client;

    @Autowired
    ExerciseJournalRepository exerciseJournalRepository;

    @Autowired
    FeedExerciseJournalImageRepository feedExerciseJournalImageRepository;

    @Autowired
    FeedExerciseJournalRepository feedExerciseJournalRepository;

    @Autowired
    ExerciseListRepository exerciseListRepository;

    @Autowired
    ExerciseHistoryRepository exerciseHistoryRepository;

    @Autowired
    ExerciseJournalHelper exerciseJournalHelper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    ExercisePartRepository exercisePartRepository;

    @Autowired
    ExerciseJournalReplicationHistoryRepository exerciseJournalReplicationHistoryRepository;

    @Autowired
    ExerciseHelper exerciseHelper;

    @Autowired
    UserHelper userHelper;

    @Autowired
    RoutineRepository routineRepository;


    ExerciseJournal exerciseJournal;
    ExerciseList exerciseList;
    ExerciseHistory exerciseHistory;
    Exercise exercise;
    User user;
    LocalDate today;

    @BeforeEach
    @Transactional
    void beforeTest() {
        user = userRepository.save(User.builder().email("ogjg@email.com").nickname("ogjgNickname").build());

        exercise = exerciseRepository.save(new Exercise("푸시 업"));
        ExercisePart exercisePart = exercisePartRepository.save(new ExercisePart(exercise, "가슴"));

        exerciseJournal = exerciseJournalRepository.save(ExerciseJournal.builder()
                .exerciseTime(new TimeTemplate(1, 10, 10))
                .exerciseEndTime(LocalDateTime.now())
                .user(user)
                .journalDate(LocalDate.now())
                .isVisible(true)
                .build());

        exerciseList = exerciseListRepository.save(ExerciseList.builder()
                .exercise(exercise)
                .exerciseJournal(exerciseJournal)
                .exerciseNum(1)
                .restTime(new TimeTemplate(1, 12, 12))
                .build());

        exerciseHistory = exerciseHistoryRepository.save(
                new ExerciseHistory(exerciseList, 1, 20, 3, false));

        today = LocalDate.now();
    }

    @Test
    @Transactional
    @DisplayName("[운동일지 생성] 빈 운동일지 생성하기")
    void createJournal() {
        ExerciseJournal result1 = exerciseJournalService.createJournal("ogjg@email.com", today.plusDays(1));

        assertThat(result1).isNotNull();
        assertThat(result1.getExerciseTime().getHours()).isEqualTo(0);
        assertThat(result1.getExerciseTime().getSeconds()).isEqualTo(0);
        assertThat(result1.getExerciseTime().getHours()).isEqualTo(0);
        assertThat(result1.getSplit()).isNull();
        assertThat(result1.getExerciseStartTime()).isNull();
        assertThat(result1.getExerciseEndTime()).isNull();
        assertThat(result1.isCompleted()).isFalse();
        assertThat(result1.isVisible()).isFalse();
    }

    @Test
    @Transactional
    @DisplayName("[내 운동일지 목록보기] 내 운동일지 목록보기")
    void userJournalLists() {
        IntStream.range(1, 10).forEach(
                i -> exerciseJournalRepository.save(ExerciseJournal.builder()
                        .exerciseTime(new TimeTemplate(1, 10, 10))
                        .exerciseEndTime(LocalDateTime.now())
                        .user(user)
                        .journalDate(LocalDate.now().plusDays(i))
                        .isVisible(true)
                        .build())
        );

        UserJournalListResponse userJournalLists = exerciseJournalService.userJournalLists(user.getEmail());
        assertThat(userJournalLists.getJournals().size()).isEqualTo(10);
    }

    @Test
    @Transactional
    @DisplayName("[운동일지 완료하기] 운동일지 완료하기")
    void exerciseJournalComplete() {
        exerciseHistory.updateHistory(new UpdateExerciseHistoryRequest(20, 10, true));

        exerciseJournalService.exerciseJournalComplete(
                exerciseJournal.getId(), user.getEmail(),
                new ExerciseJournalCompleteRequest(true,
                        new TimeTemplate(1, 10, 10)));

        assertThat(exerciseJournal.isCompleted()).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("[Exception 운동일지 완료하기] 운동기록이 모두 완료된 상태가 아니라면 완료 실패")
    void failExerciseJournalComplete() {
        assertThatThrownBy(() ->
                exerciseJournalService.exerciseJournalComplete(
                        exerciseJournal.getId(), user.getEmail(),
                        new ExerciseJournalCompleteRequest(true,
                                new TimeTemplate(1, 10, 10))))
                .isInstanceOf(NotCompletedExerciseHistory.class)
                .hasMessage(ErrorCode.NOT_COMPLETED_EXERCISE_HISTORY.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("[운동일지 공유] 운동일지 피드에 공유")
    void exerciseJournalShare() {
        List<MultipartFile> imgFiles = List.of(
                (MultipartFile) new MockMultipartFile("img", "fileName", "image/jpeg", "image content".getBytes())
        );

        exerciseJournal.journalComplete(new ExerciseJournalCompleteRequest(true, new TimeTemplate(1, 1, 1)));

        exerciseJournalService.exerciseJournalShare(
                exerciseJournal.getId(),
                user.getEmail(),
                new ExerciseJournalShareRequest(true, "무분할"),
                imgFiles);

        ExerciseJournal exerciseJournal = exerciseJournalRepository.findById(this.exerciseJournal.getId()).orElseThrow();
        FeedExerciseJournal feedJournalByJournal = feedJournalHelper.findFeedJournalByJournal(exerciseJournal);

        assertThat(exerciseJournal.isCompleted()).isTrue();
        assertThat(exerciseJournal.getSplit()).isEqualTo("무분할");
        assertThat(exerciseJournal.isVisible()).isTrue();
        assertThat(feedJournalByJournal.getExerciseJournal().getId()).isEqualTo(exerciseJournal.getId());
    }

    @Test
    @Transactional
    @DisplayName("[Exception 운동일지 공유] 피드에 일지가 이미 공유된 상태라는 에러 발생")
    void failExerciseJournalShare() {
        List<MultipartFile> imgFiles = List.of(
                (MultipartFile) new MockMultipartFile(
                        "img", "fileName", "image/jpeg", "image content".getBytes())
        );

        exerciseJournal.journalComplete(
                new ExerciseJournalCompleteRequest(true, new TimeTemplate(1, 1, 1)));

        feedExerciseJournalRepository.save(FeedExerciseJournal.builder()
                .exerciseJournal(exerciseJournal)
                .build());

        assertThatThrownBy(
                () -> exerciseJournalService.exerciseJournalShare(
                        exerciseJournal.getId(),
                        user.getEmail(),
                        new ExerciseJournalShareRequest(true, "무분할"),
                        imgFiles)
        ).isInstanceOf(AlreadyExistFeedJournal.class)
                .hasMessage(ErrorCode.ALREADY_SHARED_JOURNAL.getMessage());
    }

    @Test
    @DisplayName("[운동일지 삭제] 운동일지 삭제시 하위 목록, 기록, 피드, 피드이미지 삭제")
    @Transactional
    void deleteJournal() {
        FeedExerciseJournal feedExerciseJournal = feedExerciseJournalRepository.save(
                FeedExerciseJournal.builder()
                        .exerciseJournal(exerciseJournal)
                        .build());

        FeedExerciseJournalImage feedJournalImage = feedExerciseJournalImageRepository.save(
                new FeedExerciseJournalImage(
                        feedExerciseJournal,
                        "img.url"
                ));

        exerciseJournalService.deleteJournal(user.getEmail(), exerciseJournal.getId());

        assertThat(exerciseJournalRepository.findById(exerciseJournal.getId())).isEmpty();
        assertThat(exerciseListRepository.findById(exerciseList.getId())).isEmpty();
        assertThat(exerciseHistoryRepository.findById(exerciseHistory.getId())).isEmpty();
        assertThat(feedExerciseJournalRepository.findById(feedExerciseJournal.getId())).isEmpty();
        assertThat(feedExerciseJournalImageRepository.findById(feedJournalImage.getId())).isEmpty();
    }

    @Test
    @DisplayName("[운동목록 추가하기 기본 운동기록도 같이 생성] 운동일지에 하위 운동목록,운동기록 추가하기")
    @Transactional
    void createExerciseList() {

        List<ExerciseHistoryRequest> defaultHistory = List.of(
                ExerciseHistoryRequest.builder()
                        .counts(5)
                        .weights(20)
                        .setNum(1)
                        .build(),
                ExerciseHistoryRequest.builder()
                        .counts(3)
                        .weights(20)
                        .setNum(2)
                        .build()
        );

        ExerciseListRequest exerciseListRequest = ExerciseListRequest.builder()
                .id(exerciseJournal.getId())
                .name("푸시 업")
                .exerciseSets(defaultHistory)
                .exerciseNum(1)
                .restTime(new TimeTemplate(1, 1, 1))
                .build();

        exerciseJournalService.createExerciseList(user.getEmail(), exerciseListRequest);
        List<ExerciseList> exerciseLists = exerciseListRepository.findByExerciseJournal(exerciseJournal);
        List<ExerciseHistory> exercisehistories = exerciseHistoryRepository.findAllByExerciseList(exerciseLists.get(1));

        assertThat(exerciseLists.size()).isEqualTo(2);
        assertThat(exerciseLists.get(1).getExercise().getName()).isEqualTo("푸시 업");
        assertThat(exerciseLists.get(1).getExerciseNum()).isEqualTo(1);
        assertThat(exercisehistories.size()).isEqualTo(2);
        assertThat(exercisehistories.get(0).getSetNum()).isEqualTo(1);
        assertThat(exercisehistories.get(1).getSetNum()).isEqualTo(2);
    }

    @Test
    @DisplayName("[운동 목록 삭제] 운동목록과 그 하위 운동기록 삭제")
    @Transactional
    void deleteExerciseList() {
        exerciseJournalService.deleteExerciseList(user.getEmail(), exerciseList.getId());
        assertThat(exerciseListRepository.findByExerciseJournal(exerciseJournal)).isEmpty();
        assertThat(exerciseHistoryRepository.findAllByExerciseList(exerciseList)).isEmpty();
    }

    @Test
    @DisplayName("[운동 기록 생성하기] 운동목록에 하위 운동기록 생성하기")
    @Transactional
    void createExerciseHistory() {
        ExerciseHistoryRequest historyRequest = ExerciseHistoryRequest.builder()
                .id(exerciseList.getId())
                .setNum(1)
                .weights(10)
                .counts(3)
                .build();

        exerciseJournalService.createExerciseHistory(user.getEmail(), historyRequest);
        List<ExerciseHistory> exerciseHistories = exerciseHistoryRepository.findAllByExerciseList(exerciseList);

        assertThat(exerciseHistories.get(1).getSetNum()).isEqualTo(1);
        assertThat(exerciseHistories.get(1).getWeight()).isEqualTo(10);
        assertThat(exerciseHistories.get(1).getRepetitionCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("[운동일지 가져오기] 다른 사람의 운동일지 복사해오기")
    @Transactional
    void replicateExerciseJournal() {
        LocalDate nextDay = LocalDate.now().plusDays(1);

        FeedExerciseJournal feedExerciseJournal = feedExerciseJournalRepository.save(new FeedExerciseJournal(exerciseJournal));
        exerciseJournalService.replicateExerciseJournal(
                user.getEmail(), feedExerciseJournal.getId(),
                new ReplicationExerciseJournalRequest(LocalDate.now().plusDays(1))
        );

        ExerciseJournal replicatedJournal = exerciseJournalRepository.findByUserAndJournalDate(user, nextDay).orElseThrow();
        List<ExerciseList> exerciseLists = exerciseListRepository.findByExerciseJournal(replicatedJournal);
        List<ExerciseHistory> exerciseHistories = exerciseHistoryRepository.findAllByExerciseList(exerciseLists.get(0));

        assertThat(exerciseLists.get(0).getExercise().getName()).isEqualTo(exerciseList.getExercise().getName());
        assertThat(replicatedJournal.getId()).isNotEqualTo(exerciseJournal.getId());
        assertThat(exerciseHistories.get(0).getWeight()).isEqualTo(exerciseHistory.getWeight());
        assertThat(exerciseHistory.getId()).isNotEqualTo(exerciseHistories.get(0).getId());
    }

    @Test
    @DisplayName("[운동목록 휴식 시간 변경] 운동목록의 휴식시간 변경")
    @Transactional
    void changeExerciseListRestTime() {
        exerciseJournalService.changeExerciseListRestTime(
                user.getEmail(), exerciseList.getId(), new UpdateRestTimeRequest(new TimeTemplate(2, 2, 2)));

        ExerciseList findExerciseList = exerciseListRepository.findById(exerciseList.getId()).orElseThrow();

        assertThat(findExerciseList.getRestTime().getHours()).isEqualTo(2);
        assertThat(findExerciseList.getRestTime().getMinutes()).isEqualTo(2);
        assertThat(findExerciseList.getRestTime().getSeconds()).isEqualTo(2);
    }

    @Test
    @DisplayName("[운동기록 삭제] 운동기록 삭제하기")
    @Transactional
    void deleteExerciseHistory() {
        exerciseJournalService.deleteExerciseHistory(user.getEmail(), exerciseHistory.getId());
        assertThat(exerciseHistoryRepository.findById(exerciseHistory.getId()).isEmpty());
    }

    @Test
    @DisplayName("[운동 기록 변경]")
    @Transactional
    void updateExerciseHistory() {
        exerciseJournalService.updateExerciseHistory(
                user.getEmail(), exerciseHistory.getId(),
                new UpdateExerciseHistoryRequest(20, 3, true)
        );
        ExerciseHistory findExerciseHistory = exerciseHistoryRepository.findById(exerciseHistory.getId()).orElseThrow();
        assertThat(findExerciseHistory.getWeight()).isEqualTo(20);
        assertThat(findExerciseHistory.getRepetitionCount()).isEqualTo(3);
        assertThat(findExerciseHistory.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("[운동일지 상세 조회] 내 운동일지 상세 조회")
    @Transactional
    void userJournalDetail(){
        UserJournalDetailResponse userJournalDetail = exerciseJournalService.userJournalDetail(LocalDate.now(), user.getEmail());

        assertThat(userJournalDetail.getJournal().getId()).isEqualTo(exerciseJournal.getId());
        assertThat(userJournalDetail.getJournal().getExercises().get(0).getId()).isEqualTo(exerciseList.getId());
        assertThat(userJournalDetail.getJournal().getExercises().get(0).getExerciseSets().get(0).getId()).isEqualTo(exerciseHistory.getId());

    }
}