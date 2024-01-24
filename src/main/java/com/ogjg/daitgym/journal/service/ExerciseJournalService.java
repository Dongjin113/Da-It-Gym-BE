package com.ogjg.daitgym.journal.service;

import com.ogjg.daitgym.common.exception.feed.AlreadyExistFeedJournal;
import com.ogjg.daitgym.common.exception.journal.NotCompletedExerciseJournal;
import com.ogjg.daitgym.domain.User;
import com.ogjg.daitgym.domain.feed.FeedExerciseJournal;
import com.ogjg.daitgym.domain.journal.ExerciseHistory;
import com.ogjg.daitgym.domain.journal.ExerciseJournal;
import com.ogjg.daitgym.domain.journal.ExerciseList;
import com.ogjg.daitgym.exercise.service.ExerciseHelper;
import com.ogjg.daitgym.feed.service.FeedJournalHelper;
import com.ogjg.daitgym.journal.dto.request.*;
import com.ogjg.daitgym.journal.dto.response.UserJournalDetailResponse;
import com.ogjg.daitgym.journal.dto.response.UserJournalListResponse;
import com.ogjg.daitgym.journal.dto.response.dto.UserJournalDetailDto;
import com.ogjg.daitgym.journal.dto.response.dto.UserJournalDetailExerciseListDto;
import com.ogjg.daitgym.journal.dto.response.dto.UserJournalListDto;
import com.ogjg.daitgym.journal.repository.exercisehistory.ExerciseHistoryRepository;
import com.ogjg.daitgym.journal.repository.exerciselist.ExerciseListRepository;
import com.ogjg.daitgym.journal.repository.journal.ExerciseJournalRepository;
import com.ogjg.daitgym.user.service.UserHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseJournalService {

    private final ExerciseJournalRepository exerciseJournalRepository;
    private final ExerciseListRepository exerciseListRepository;
    private final ExerciseHistoryRepository exerciseHistoryRepository;
    private final ExerciseHelper exerciseHelper;
    private final FeedJournalHelper feedJournalHelper;
    private final ExerciseJournalHelper exerciseJournalHelper;
    private final UserHelper userHelper;

    /**
     * 빈 운동일지 생성하기
     * 해당 날짜에 생성된 일지가 있으면 예외 발생
     */
    @Transactional
    public ExerciseJournal createJournal(String email, LocalDate journalDate) {
        return exerciseJournalHelper.createJournal(email, journalDate);
    }

    /**
     * 내 운동일지 목록보기
     */
    @Transactional(readOnly = true)
    public UserJournalListResponse userJournalLists(
            String email
    ) {
        User user = userHelper.findUserByEmail(email);
        List<UserJournalListDto> userJournalListDtoList = exerciseJournalRepository.findAllByUser(user)
                .stream()
                .map(UserJournalListDto::new)
                .toList();

        return new UserJournalListResponse(userJournalListDtoList);
    }

    /**
     * 운동일지 완료하기
     */
    @Transactional
    public void exerciseJournalComplete(
            Long journalId, String email,
            ExerciseJournalCompleteRequest exerciseJournalCompleteRequest
    ) {
        exerciseJournalHelper.isAuthorizedForJournal(email, journalId);
        ExerciseJournal exerciseJournal = exerciseJournalHelper.findExerciseJournal(journalId);
        exerciseJournalHelper.checkAllExerciseHistoriesCompleted(exerciseJournal);
        exerciseJournal.journalComplete(exerciseJournalCompleteRequest);
    }

    /**
     * 운동일지 공유하기
     * 운동일지 공개 여부 확인
     * 피드가 이미 존재한다면 공유된 일지라는 예외발생
     */
    @Transactional
    public void exerciseJournalShare(
            Long journalId, String email,
            ExerciseJournalShareRequest exerciseJournalShareRequest,
            List<MultipartFile> imgFiles
    ) {
        ExerciseJournal exerciseJournal = exerciseJournalHelper.isAuthorizedForJournal(email, journalId);

        if (!exerciseJournal.isCompleted())
            throw new NotCompletedExerciseJournal();

        if (feedJournalHelper.checkExistFeedExerciseJournalByExerciseJournal(exerciseJournal))
            throw new AlreadyExistFeedJournal();

        exerciseJournal.journalShareToFeed(exerciseJournalShareRequest);

        if (exerciseJournal.isVisible()) {
            feedJournalHelper.shareJournalFeed(exerciseJournal, imgFiles);
        }
    }

    /**
     * 운동일지 삭제시
     * 피드 운동일지, 피드 좋아요, 피드 댓글 삭제
     * 운동기록, 운동목록, 운동일지 삭제하기
     * 일지가 공유된 상태가 아니라면 피드에 대한 삭제가 발생하지 않음
     */
    @Transactional
    public void deleteJournal(String email, Long journalId) {
        ExerciseJournal journal = exerciseJournalHelper.isAuthorizedForJournal(email, journalId);

        if (journal.isVisible()) {
            FeedExerciseJournal feedJournal = feedJournalHelper.findFeedJournalByJournal(journal);
            feedJournalHelper.deleteFeedJournal(email, feedJournal.getId());
        }

        List<ExerciseList> exerciseLists = exerciseJournalHelper.findExerciseLists(journal);
        exerciseLists.forEach(exerciseHistoryRepository::deleteAllByExerciseList);
        exerciseListRepository.deleteAllByExerciseJournal(journal);

        exerciseJournalRepository.delete(journal);
    }

    /**
     * 운동일지에 운동목록 추가하기
     * default 운동기록도 같이 생성됌
     */
    @Transactional
    public void createExerciseList(
            String email,
            ExerciseListRequest exerciseListRequest
    ) {

        ExerciseJournal userJournal = exerciseJournalHelper.isAuthorizedForJournal(email, exerciseListRequest.getId());

        ExerciseList exerciseList = exerciseJournalHelper.saveExerciseList(
                userJournal,
                exerciseHelper.findExercise(exerciseListRequest.getName()),
                exerciseListRequest
        );

        exerciseListRequest.getExerciseSets()
                .forEach(exerciseHistoryRequest -> exerciseHistoryRepository.save(
                        ExerciseHistory.createExerciseHistory(exerciseList, exerciseHistoryRequest)
                ));
    }

    /**
     * 운동목록 삭제하기
     * 운동기록 삭제
     */
    @Transactional
    public void deleteExerciseList(String email, Long exerciseListId) {
        ExerciseList exerciseList = exerciseJournalHelper.findExerciseList(exerciseListId);

        exerciseJournalHelper.isAuthorizedForJournal(email, exerciseList.getExerciseJournal().getId());

        exerciseHistoryRepository.deleteAllByExerciseList(exerciseList);
        exerciseListRepository.delete(exerciseList);
    }

    /**
     * 운동목록에 운동 기록 생성하기
     */
    @Transactional
    public ExerciseHistory createExerciseHistory(String email, ExerciseHistoryRequest exerciseHistoryRequest) {

        ExerciseList exerciseList = exerciseJournalHelper.findExerciseList(exerciseHistoryRequest.getId());
        exerciseJournalHelper.isAuthorizedForJournal(email, exerciseList.getExerciseJournal().getId());

        return exerciseHistoryRepository.save(
                ExerciseHistory.createExerciseHistory(exerciseList, exerciseHistoryRequest)
        );
    }

    /**
     * 다른 사람의 운동일지 가져오기
     */
    @Transactional
    public void replicateExerciseJournal(
            String email, Long originalFeedJournalId,
            ReplicationExerciseJournalRequest replicationExerciseJournalRequest
    ) {

        ExerciseJournal originalJournal = feedJournalHelper.findExerciseJournalByFeedJournalId(originalFeedJournalId);
        //쿼리를 한방에 가져오기
        exerciseJournalRepository.fetchCompleteExerciseJournalByJournalId(originalJournal.getId());
        List<ExerciseList> originalExerciseLists = exerciseJournalHelper.findExerciseLists(originalJournal);

        ExerciseJournal replicatedUserJournal =
                exerciseJournalHelper.getReplicatedExerciseJournal(replicationExerciseJournalRequest.getJournalDate(), email);

        exerciseJournalHelper.replicateExerciseListAndHistoryByJournal(replicatedUserJournal, originalExerciseLists);
        exerciseJournalHelper.saveReplicationHistory(email, originalJournal, replicatedUserJournal);
    }

    /**
     * 루틴에서 일지 가져오기
     */
    @Transactional
    public void replicateExerciseJournalFromRoutine(
            ReplicationRoutineRequest replicationRoutineRequest, String email
    ) {
        replicationRoutineRequest.getRoutines()
                .forEach(replicationRoutineRequestDto -> {
                            ExerciseJournal replicatedUserJournal = exerciseJournalHelper.getReplicatedExerciseJournal(replicationRoutineRequestDto.getJournalDate(), email);
                            exerciseJournalHelper.replicateExerciseListAndHistoryByRoutine(replicatedUserJournal, replicationRoutineRequestDto);
                        }
                );
    }

    /**
     * 운동 목록 휴식시간 변경
     */
    @Transactional
    public void changeExerciseListRestTime(
            String email, Long exerciseListId,
            UpdateRestTimeRequest updateRestTimeRequest
    ) {
        ExerciseList exerciseList = exerciseJournalHelper.findExerciseList(exerciseListId);
        exerciseJournalHelper.isAuthorizedForJournal(email, exerciseList.getExerciseJournal().getId());
        exerciseList.changeRestTime(updateRestTimeRequest);
    }

    /**
     * 운동기록 삭제하기
     */
    @Transactional
    public void deleteExerciseHistory(String email, Long exerciseHistoryId) {
        ExerciseHistory exerciseHistory = exerciseJournalHelper.findExerciseHistory(exerciseHistoryId);

        exerciseJournalHelper.isAuthorizedForJournal(
                email,
                exerciseHistory.getExerciseList()
                        .getExerciseJournal()
                        .getId()
        );

        exerciseHistoryRepository.delete(exerciseHistory);
    }

    /**
     * 운동기록 변경하기
     */
    @Transactional
    public void updateExerciseHistory(
            String email, Long exerciseHistoryId,
            UpdateExerciseHistoryRequest updateExerciseHistoryRequest
    ) {
        ExerciseHistory exerciseHistory = exerciseJournalHelper.findExerciseHistory(exerciseHistoryId);

        exerciseJournalHelper.isAuthorizedForJournal(email, exerciseHistory.getExerciseList().getExerciseJournal().getId());

        exerciseHistory.updateHistory(updateExerciseHistoryRequest);
    }

    /**
     * 내 운동일지 상세보기
     * todo 개별조회로 인한 성능이슈 발생 가능성이 보임 추후 Join을 통해 한번에 가져오도록 개선필요로 보임
     * => JOIN을 통해 가져오는게 정말 성능적으로 개선이 되는가? 중복된 데이터는?
     */
    @Transactional(readOnly = true)
    public UserJournalDetailResponse userJournalDetail(
            LocalDate journalDate, String email
    ) {
        User user = userHelper.findUserByEmail(email);
        ExerciseJournal exerciseJournal = exerciseJournalHelper.findExerciseJournal(user, journalDate);
        exerciseJournalHelper.isAuthorizedForJournal(email, exerciseJournal.getId());

        List<ExerciseList> journalList = exerciseJournalHelper.findExerciseLists(exerciseJournal);
        List<UserJournalDetailExerciseListDto> exerciseListsDto = exerciseJournalHelper.exerciseListsChangeUserJournalDetailsDto(journalList);
        UserJournalDetailDto userJournalDetailDto = new UserJournalDetailDto(exerciseJournal, exerciseListsDto);

        return new UserJournalDetailResponse(userJournalDetailDto);
    }
}
