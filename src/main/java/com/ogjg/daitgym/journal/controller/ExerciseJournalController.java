package com.ogjg.daitgym.journal.controller;

import com.ogjg.daitgym.common.exception.ErrorCode;
import com.ogjg.daitgym.common.response.ApiResponse;
import com.ogjg.daitgym.config.security.details.OAuth2JwtUserDetails;
import com.ogjg.daitgym.domain.journal.ExerciseHistory;
import com.ogjg.daitgym.domain.journal.ExerciseJournal;
import com.ogjg.daitgym.journal.dto.request.*;
import com.ogjg.daitgym.journal.dto.response.UserJournalDetailResponse;
import com.ogjg.daitgym.journal.dto.response.UserJournalListResponse;
import com.ogjg.daitgym.journal.service.ExerciseJournalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/journals")
@RequiredArgsConstructor
public class ExerciseJournalController {

    private final ExerciseJournalService exerciseJournalService;

    /**
     * 빈 일지 생성하기
     */
    @PostMapping
    public ApiResponse<Map<String, Long>> createJournal(
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails,
            @RequestBody CreateJournalRequest createJournalRequest
    ) {
        ExerciseJournal journal = exerciseJournalService.createJournal(
                userDetails.getEmail(), createJournalRequest.getJournalDate()
        );

        return new ApiResponse<>(
                ErrorCode.SUCCESS,
                Map.of("id", journal.getId())
        );
    }

    /**
     * 일지에 운동 추가하기
     */
    @PostMapping("/exercise-list")
    public ApiResponse<Void> addExerciseToJournal(
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails,
            @RequestBody ExerciseListRequest exerciseListRequest
    ) {
        exerciseJournalService.createExerciseList(
                userDetails.getEmail(), exerciseListRequest
        );

        return new ApiResponse<>(ErrorCode.SUCCESS);
    }

    /**
     * 운동 목록에 운동기록 추가하기
     */
    @PostMapping("/exercise-history")
    public ApiResponse<Map<String, Long>> addExerciseHistoryToExerciseList(
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails,
            @RequestBody ExerciseHistoryRequest exerciseHistoryRequest
    ) {
        ExerciseHistory exerciseHistory = exerciseJournalService.createExerciseHistory(
                userDetails.getEmail(), exerciseHistoryRequest
        );

        return new ApiResponse<>(
                ErrorCode.SUCCESS,
                Map.of("id", exerciseHistory.getId())
        );
    }

    /**
     * 운동 일지 삭제하기
     */
    @DeleteMapping("/{journalId}")
    public ApiResponse<Void> deleteJournal(
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails,
            @PathVariable("journalId") Long journalId
    ) {
        exerciseJournalService.deleteJournal(userDetails.getEmail(), journalId);

        return new ApiResponse<>(ErrorCode.SUCCESS);
    }

    /**
     * 운동 목록 삭제하기
     */
    @DeleteMapping("/exercise-list/{exerciseListId}")
    public ApiResponse<Void> deleteExerciseList(
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails,
            @PathVariable("exerciseListId") Long exerciseListId
    ) {
        exerciseJournalService.deleteExerciseList(userDetails.getEmail(), exerciseListId);

        return new ApiResponse<>(ErrorCode.SUCCESS);
    }

    /**
     * 운동 기록 삭제하기
     */
    @DeleteMapping("/exercise-history/{exerciseHistoryId}")
    public ApiResponse<Void> deleteExerciseHistory(
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails,
            @PathVariable("exerciseHistoryId") Long exerciseHistoryId
    ) {
        exerciseJournalService.deleteExerciseHistory(
                userDetails.getEmail(), exerciseHistoryId
        );

        return new ApiResponse<>(ErrorCode.SUCCESS);
    }

    /**
     * 운동기록 변경하기
     */
    @PatchMapping("/exercise-history/{exerciseHistoryId}")
    public ApiResponse<Void> updateExerciseHistory(
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails,
            @PathVariable("exerciseHistoryId") Long exerciseHistoryId,
            @RequestBody UpdateExerciseHistoryRequest updateExerciseHistoryRequest
    ) {
        exerciseJournalService.updateExerciseHistory(
                userDetails.getEmail(), exerciseHistoryId, updateExerciseHistoryRequest
        );

        return new ApiResponse<>(ErrorCode.SUCCESS);
    }

    /**
     * 내 운동일지 목록 조회
     */
    @GetMapping
    public ApiResponse<UserJournalListResponse> userJournalList(
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails
    ) {
        return new ApiResponse<>(
                ErrorCode.SUCCESS,
                exerciseJournalService.userJournalLists(userDetails.getEmail())
        );
    }

    /**
     * 내 운동일지 상세보기
     */
    @GetMapping("/{journalDate}")
    public ApiResponse<UserJournalDetailResponse> userJournalDetail(
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails,
            @PathVariable("journalDate") LocalDate journalDate
    ) {
        return new ApiResponse<>(
                ErrorCode.SUCCESS,
                exerciseJournalService.userJournalDetail(journalDate, userDetails.getEmail())
        );
    }

    /**
     * 내 운동일지 상세보기 Redis에서 조회
     */
    @GetMapping("/{journalDate}/redis")
    public ApiResponse<UserJournalDetailResponse> userJournalDetailRedis(
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails,
            @PathVariable("journalDate") LocalDate journalDate
    ) {
        return new ApiResponse<>(
                ErrorCode.SUCCESS,
                exerciseJournalService.userJournalDetailByRedis(
                        journalDate,
                        userDetails.getEmail()
                )
        );
    }

    /**
     * 내 운동목록 휴식시간 변경
     */
    @PostMapping("/exercise-list/{exerciseListId}/rest-time")
    public ApiResponse<Void> changeExerciseListRestTime(
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails,
            @PathVariable("exerciseListId") Long exerciseListId,
            @RequestBody UpdateRestTimeRequest updateRestTimeRequest
    ) {
        exerciseJournalService.changeExerciseListRestTime(
                userDetails.getEmail(), exerciseListId, updateRestTimeRequest
        );

        return new ApiResponse<>(ErrorCode.SUCCESS);
    }

    /**
     * 운동일지 작성완료
     */
    @PatchMapping("/{journalId}/complete")
    public ApiResponse<Void> exerciseJournalComplete(
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails,
            @PathVariable("journalId") Long journalId,
            @RequestBody ExerciseJournalCompleteRequest exerciseJournalCompleteRequest
    ) {
        exerciseJournalService.exerciseJournalComplete(
                journalId, userDetails.getEmail(), exerciseJournalCompleteRequest
        );

        return new ApiResponse<>(ErrorCode.SUCCESS);
    }

    /**
     * 운동일지 작성완료후 Redis에 저장
     */
    @PatchMapping("/{journalId}/complete/redis")
    public ApiResponse<Void> exerciseJournalCompleteRedis(
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails,
            @PathVariable("journalId") Long journalId,
            @RequestBody ExerciseJournalCompleteRequest exerciseJournalCompleteRequest
    ) {
        exerciseJournalService.exerciseJournalCompleteAndSaveRedis(
                journalId, userDetails.getEmail(), exerciseJournalCompleteRequest
        );

        return new ApiResponse<>(ErrorCode.SUCCESS);
    }

    /**
     * 운동일지 피드에 공유
     */
    @PatchMapping("/{journalId}/share")
    public ApiResponse<Void> exerciseJournalShare(
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails,
            @PathVariable("journalId") Long journalId,
            @RequestPart("share") ExerciseJournalShareRequest exerciseJournalShareRequest,
            @RequestPart(value = "imgFiles", required = false) List<MultipartFile> imgFiles
    ) {
        exerciseJournalService.exerciseJournalShare(
                journalId, userDetails.getEmail(), exerciseJournalShareRequest, imgFiles
        );

        return new ApiResponse<>(ErrorCode.SUCCESS);
    }

    /**
     * 피드 운동일지에서 다른사람의 일지 가져오기
     */
    @PostMapping("/{feedJournalId}/replication")
    public ApiResponse<Void> replicationExerciseJournal(
            @PathVariable("feedJournalId") Long feedJournalId,
            @RequestBody ReplicationExerciseJournalRequest replicationExerciseJournalRequest,
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails
    ) {
        exerciseJournalService.replicateExerciseJournal(
                userDetails.getEmail(), feedJournalId, replicationExerciseJournalRequest
        );

        return new ApiResponse<>(ErrorCode.SUCCESS);
    }

    /**
     * 피드 운동일지에서 다른사람의 일지 Redis에서 가져오기
     */
    @PostMapping("/{feedJournalId}/replication/redis")
    public ApiResponse<Void> replicationExerciseJournalByRedis(
            @PathVariable("feedJournalId") Long feedJournalId,
            @RequestBody ReplicationExerciseJournalRequest replicationExerciseJournalRequest,
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails
    ) {
        exerciseJournalService.replicateExerciseJournalByRedis(
                userDetails.getEmail(),
                feedJournalId,
                replicationExerciseJournalRequest
        );

        return new ApiResponse<>(ErrorCode.SUCCESS);
    }

    /**
     * 피드 운동일지에서 다른사람의 일지 가져오기
     */
    @PostMapping("/replication/routine")
    public ApiResponse<Void> replicationExerciseJournalFromRoutine(
            @RequestBody ReplicationRoutineRequest replicationRoutineRequest,
            @AuthenticationPrincipal OAuth2JwtUserDetails userDetails
    ) {
        exerciseJournalService.replicateExerciseJournalFromRoutine(
                replicationRoutineRequest, userDetails.getEmail()
        );

        return new ApiResponse<>(ErrorCode.SUCCESS);
    }
}
