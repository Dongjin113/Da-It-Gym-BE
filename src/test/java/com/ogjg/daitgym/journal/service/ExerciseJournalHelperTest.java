package com.ogjg.daitgym.journal.service;

import com.ogjg.daitgym.common.exception.ErrorCode;
import com.ogjg.daitgym.common.exception.journal.AlreadyExistExerciseJournal;
import com.ogjg.daitgym.common.exception.journal.NotCompletedExerciseHistory;
import com.ogjg.daitgym.common.exception.journal.UserNotAuthorizedForJournal;
import com.ogjg.daitgym.domain.TimeTemplate;
import com.ogjg.daitgym.domain.User;
import com.ogjg.daitgym.domain.exercise.Exercise;
import com.ogjg.daitgym.domain.exercise.ExercisePart;
import com.ogjg.daitgym.domain.journal.ExerciseHistory;
import com.ogjg.daitgym.domain.journal.ExerciseJournal;
import com.ogjg.daitgym.domain.journal.ExerciseList;
import com.ogjg.daitgym.exercise.repository.ExercisePartRepository;
import com.ogjg.daitgym.exercise.repository.ExerciseRepository;
import com.ogjg.daitgym.exercise.service.ExerciseHelper;
import com.ogjg.daitgym.journal.dto.response.dto.UserJournalDetailExerciseHistoryDto;
import com.ogjg.daitgym.journal.dto.response.dto.UserJournalDetailExerciseListDto;
import com.ogjg.daitgym.journal.repository.exercisehistory.ExerciseHistoryRepository;
import com.ogjg.daitgym.journal.repository.exerciselist.ExerciseListRepository;
import com.ogjg.daitgym.journal.repository.journal.ExerciseJournalReplicationHistoryRepository;
import com.ogjg.daitgym.journal.repository.journal.ExerciseJournalRepository;
import com.ogjg.daitgym.routine.repository.RoutineRepository;
import com.ogjg.daitgym.user.repository.UserRepository;
import com.ogjg.daitgym.user.service.UserHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class ExerciseJournalHelperTest {

    @Autowired
    ExerciseJournalService exerciseJournalService;

    @Autowired
    ExerciseJournalRepository exerciseJournalRepository;

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

    @BeforeEach
    @Transactional
    void beforeTest() {
        user = userRepository.save(User.builder().email("ogjg1@email.com").nickname("ogjgNickname1").build());

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
                new ExerciseHistory(exerciseList, 1, 20, 3, true));
    }

    @Test
    @Transactional
    @DisplayName("[운동일지 검색] Id로 운동일지 검색")
    void findExerciseJournalById() {
        ExerciseJournal findExerciseJournal = exerciseJournalHelper.findExerciseJournal(exerciseJournal.getId());
        assertThat(findExerciseJournal.getId()).isEqualTo(exerciseJournal.getId());
    }

    @Test
    @Transactional
    @DisplayName("[운동일지 검색] 유저와 일지날짜로 운동일지 검색")
    void findExerciseJournalByUserAndJournalDate() {
        LocalDate nowDate = LocalDate.now();
        ExerciseJournal findExerciseJournal = exerciseJournalHelper.findExerciseJournal(user, nowDate);
        assertThat(findExerciseJournal.getId()).isEqualTo(exerciseJournal.getId());
    }

    @Test
    @Transactional
    @DisplayName("[운동일지 중복 확인] 유저와 일지날짜로 운동일지 중복 확인")
    void checkExistExerciseJournal() {
        LocalDate nowDate = LocalDate.now();
        assertThat(exerciseJournalHelper.checkExistExerciseJournal(user, nowDate)).isTrue();
        assertThat(exerciseJournalHelper.checkExistExerciseJournal(
                user, LocalDate.now().plusDays(1))).isFalse();
    }

    @Test
    @Transactional
    @DisplayName("[일지 권한 확인] 운동일지 작성자인지 확인")
    void isAuthorizedForJournal() {
        ExerciseJournal authorizedJournal =
                exerciseJournalHelper.isAuthorizedForJournal(user.getEmail(), exerciseJournal.getId());
        assertThat(authorizedJournal).isNotNull();
        assertThat(authorizedJournal.getId()).isEqualTo(exerciseJournal.getId());
    }

    @Test
    @Transactional
    @DisplayName("[Exception 일지 권한 없음] 운동일지 권한이 없으면 오류발생")
    void notAuthorizedForJournal() {
        assertThatThrownBy(
                () -> exerciseJournalHelper.isAuthorizedForJournal("noEmail", exerciseJournal.getId()))
                .isInstanceOf(UserNotAuthorizedForJournal.class)
                .hasMessage(ErrorCode.USER_NOT_AUTHORIZED_JOURNAL.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("[운동 목록 검색] 운동 목록 Id로 운동일지 목록 검색")
    void findExerciseListById() {
        ExerciseList findExerciseList = exerciseJournalHelper.findExerciseList(exerciseList.getId());
        assertThat(findExerciseList.getId()).isEqualTo(exerciseList.getId());
        assertThat(findExerciseList.getExercise().getName()).isEqualTo(exerciseList.getExercise().getName());
    }

    @Test
    @Transactional
    @DisplayName("[운동 기록 검색] 운동 기록 Id로 운동 기록 검색")
    void findExerciseHistoryById() {
        ExerciseHistory findExerciseHistory1 = exerciseJournalHelper.findExerciseHistory(exerciseHistory.getId());
        assertThat(findExerciseHistory1.getId()).isEqualTo(exerciseHistory.getId());
        assertThat(findExerciseHistory1.getExerciseList().getId()).isEqualTo(exerciseHistory.getExerciseList().getId());
    }

    @Test
    @Transactional
    @DisplayName("[운동일지 하위 운동목록 검색] 운동일지로 운동 목록 검색")
    void findExerciseListsByExerciseJournal() {
        ExerciseList exerciseList1 = exerciseListRepository.save(ExerciseList.builder()
                .exercise(exercise)
                .exerciseJournal(exerciseJournal)
                .exerciseNum(2)
                .restTime(new TimeTemplate(1, 12, 12))
                .build());

        List<ExerciseList> exerciseLists = exerciseJournalHelper.findExerciseLists(exerciseJournal);

        assertThat(exerciseLists).hasSize(2);
        assertThat(exerciseLists.get(0).getId()).isEqualTo(exerciseList.getId());
        assertThat(exerciseLists.get(1).getId()).isEqualTo(exerciseList1.getId());
    }

    @Test
    @Transactional
    @DisplayName("[운동목록 하위 운동기록 검색] 운동목록으로 운동기록들 검색하기")
    void findExerciseHistoriesByExerciseList() {
        ExerciseHistory exerciseHistory1 = exerciseHistoryRepository.save(
                new ExerciseHistory(exerciseList, 2, 20, 5, false));
        List<ExerciseHistory> exerciseHistories = exerciseJournalHelper.findExerciseHistories(exerciseList);

        assertThat(exerciseHistories.get(0).getId()).isEqualTo(exerciseHistory.getId());
        assertThat(exerciseHistories.get(1).getId()).isEqualTo(exerciseHistory1.getId());
    }

    @Test
    @Transactional
    @DisplayName("[운동기록 완료 검증]운동기록들이 완료된 상태인지 검증")
    void checkAllExerciseHistoriesCompleted() {
        ExerciseHistory exerciseHistory1 = exerciseHistoryRepository.save(
                new ExerciseHistory(exerciseList, 2, 20, 5, true));

        assertThatCode(() -> exerciseJournalHelper.checkAllExerciseHistoriesCompleted(exerciseJournal))
                .doesNotThrowAnyException();
    }

    @Test
    @Transactional
    @DisplayName("[Exception 운동기록 완료 검증 실패] 운동기록들이 완료된 상태인지 검증 실패")
    void failCheckAllExerciseHistoriesCompleted() {
        ExerciseHistory exerciseHistory1 = exerciseHistoryRepository.save(
                new ExerciseHistory(exerciseList, 2, 20, 5, false));
        assertThatThrownBy(() -> exerciseJournalHelper.checkAllExerciseHistoriesCompleted(exerciseJournal))
                .isInstanceOf(NotCompletedExerciseHistory.class)
                .hasMessage(ErrorCode.NOT_COMPLETED_EXERCISE_HISTORY.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("[운동일지 공개여부] 공개된 운동일지인지 확인")
    void checkExerciseJournalDisclosure() {
        assertThatCode(() -> exerciseJournalHelper.checkExerciseJournalDisclosure(exerciseJournal.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    @Transactional
    @DisplayName("[Exception 운동일지 비공개 에러] 공개된 운동일지가 아니라면 에러발생")
    void failCheckExerciseJournalDisclosure() {
        ExerciseJournal exerciseJournal1 = exerciseJournalRepository.save(ExerciseJournal.builder()
                .exerciseTime(new TimeTemplate(1, 10, 10))
                .exerciseEndTime(LocalDateTime.now())
                .user(user)
                .journalDate(LocalDate.now().plusDays(1))
                .isVisible(false)
                .build());

        assertThatThrownBy(() -> exerciseJournalHelper.checkExerciseJournalDisclosure(exerciseJournal1.getId()))
                .isInstanceOf(UserNotAuthorizedForJournal.class)
                .hasMessage("공개된 운동일지가 아닙니다");
    }

    @Test
    @Transactional
    @DisplayName("[운동 목록 변환]운동 목록들을 DTO로 변환")
    void exerciseListsChangeUserJournalDetailsDto() {
        ExerciseList exerciseList1 = exerciseListRepository.save(ExerciseList.builder()
                .exercise(exercise)
                .exerciseJournal(exerciseJournal)
                .exerciseNum(2)
                .restTime(new TimeTemplate(1, 12, 12))
                .build());

        List<ExerciseList> exerciseLists = List.of(
                exerciseList,
                exerciseList1
        );

        List<UserJournalDetailExerciseListDto> result = exerciseJournalHelper.exerciseListsChangeUserJournalDetailsDto(exerciseLists);
        assertThat(result.get(0)).isInstanceOf(UserJournalDetailExerciseListDto.class);
    }

    @Test
    @Transactional
    @DisplayName("[운동 기록 변환]운동 기록들을 DTO로 변환")
    void exerciseHistoriesChangeUserJournalDetailsDto() {
        ExerciseHistory exerciseHistory1 = exerciseHistoryRepository.save(
                new ExerciseHistory(exerciseList, 1, 20, 3, true));

        List<ExerciseHistory> exerciseHistories = List.of(
                exerciseHistory,
                exerciseHistory1
        );

        List<UserJournalDetailExerciseHistoryDto> result = exerciseJournalHelper.exerciseHistoriesChangeUserJournalDetailsDto(exerciseList);
        assertThat(result.get(0)).isInstanceOf(UserJournalDetailExerciseHistoryDto.class);
    }

    @Test
    @Transactional
    @DisplayName("[운동일지 생성 또는 반환] 운동일지가 존재한다면 반환 존재하지않는다면 새 생성")
    void getReplicatedExerciseJournal() {
        ExerciseJournal result1 = exerciseJournalHelper.getReplicatedExerciseJournal(
                LocalDate.now(), "ogjg1@email.com");
        ExerciseJournal result2 = exerciseJournalHelper.getReplicatedExerciseJournal(
                LocalDate.now().minusDays(1), "ogjg1@email.com");

        assertThat(result1.getId()).isEqualTo(exerciseJournal.getId());
        assertThat(result2.getId()).isNotEqualTo(exerciseJournal.getId());
    }

    @Test
    @Transactional
    @DisplayName("[빈 운동일지 생성] 빈 운동일지 생성 해당 날짜에 존재한다면 예외 발생")
    void createJournal() {
        ExerciseJournal journal = exerciseJournalHelper.createJournal("ogjg1@email.com", LocalDate.now().plusDays(1));

        assertThat(journal.getExerciseTime().getHours()).isEqualTo(0);
        assertThat(journal.getExerciseTime().getMinutes()).isEqualTo(0);
        assertThat(journal.getExerciseTime().getSeconds()).isEqualTo(0);
        assertThat(journal.getSplit()).isNull();
        assertThat(journal.getExerciseStartTime()).isNull();
        assertThat(journal.getExerciseEndTime()).isNull();
    }

    @Test
    @Transactional
    @DisplayName("[Exception 빈 운동일지 생성] 빈 운동일지 생성 해당 날짜에 존재해 예외 발생")
    void exceptionCreateJournal() {
        assertThatThrownBy(() -> exerciseJournalHelper.createJournal("ogjg1@email.com", LocalDate.now()))
                .isInstanceOf(AlreadyExistExerciseJournal.class)
                .hasMessage(ErrorCode.ALREADY_EXIST_EXERCISE_JOURNAL.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("[운동일지 복사하기] 다른 운동일지의 운동목록과 하위 운동기록 내 일지로 복사해서 가져오기")
    void replicateExerciseListAndHistoryByJournal() {
        ExerciseJournal replicatedJournal = exerciseJournalRepository.save(ExerciseJournal.builder()
                .exerciseTime(new TimeTemplate(1, 10, 10))
                .exerciseEndTime(LocalDateTime.now())
                .user(user)
                .journalDate(LocalDate.now().plusDays(1))
                .isVisible(true)
                .build());

        List<ExerciseList> originalJournalExerciseLists = exerciseListRepository.findByExerciseJournal(exerciseJournal);
        exerciseJournalHelper.replicateExerciseListAndHistoryByJournal(replicatedJournal, originalJournalExerciseLists);
        List<ExerciseList> replicatedExerciseList = exerciseListRepository.findByExerciseJournal(replicatedJournal);
        List<ExerciseHistory> replicatedExerciseHistoriesByFirstReplicatedExerciseList = exerciseHistoryRepository.findAllByExerciseList(replicatedExerciseList.get(0));

        assertThat(exerciseJournal.getId()).isNotEqualTo(replicatedJournal.getId());

        assertThat(exerciseList.getExercise().getName())
                .isEqualTo(replicatedExerciseList.get(0).getExercise().getName());
        assertThat(exerciseList.getId())
                .isNotEqualTo(replicatedExerciseList.get(0).getId());

        assertThat(exerciseHistory.getWeight())
                .isEqualTo(replicatedExerciseHistoriesByFirstReplicatedExerciseList.get(0).getWeight());
        assertThat(exerciseHistory.getId())
                .isNotEqualTo(replicatedExerciseHistoriesByFirstReplicatedExerciseList.get(0).getId());
    }

    @Test
    @Transactional
    @DisplayName("[운동일지 복사하기] 다른 운동일지의 운동목록과 하위 운동기록 내 일지로 복사해서 가져오기")
    void replicateExerciseListByJournal() {
        ExerciseJournal replicatedJournal = exerciseJournalRepository.save(ExerciseJournal.builder()
                .exerciseTime(new TimeTemplate(1, 10, 10))
                .exerciseEndTime(LocalDateTime.now().plusDays(1))
                .user(user)
                .journalDate(LocalDate.now().plusDays(1))
                .isVisible(true)
                .build());

        ExerciseJournalHelper exerciseJournalHelper = new ExerciseJournalHelper(
                exerciseJournalReplicationHistoryRepository,
                exerciseJournalRepository, exerciseListRepository,
                exerciseHistoryRepository, exerciseHelper,
                routineRepository, userHelper
        );

        ReflectionTestUtils.invokeMethod(
                exerciseJournalHelper, "replicateExerciseListByJournal",
                replicatedJournal, exerciseList);

        List<ExerciseList> replicatedExerciseList = exerciseListRepository.findByExerciseJournal(replicatedJournal);

        assertThat(replicatedExerciseList.get(0).getId()).isNotEqualTo(exerciseList.getId());
        assertThat(replicatedExerciseList.get(0).getExercise().getName()).isEqualTo(exerciseList.getExercise().getName());
    }


}