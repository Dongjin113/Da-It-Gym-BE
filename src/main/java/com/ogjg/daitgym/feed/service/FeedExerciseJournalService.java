package com.ogjg.daitgym.feed.service;

import com.ogjg.daitgym.comment.feedExerciseJournal.exception.NotFoundFeedJournal;
import com.ogjg.daitgym.common.exception.feed.AlreadyExistFeedJournalCollection;
import com.ogjg.daitgym.domain.User;
import com.ogjg.daitgym.domain.feed.FeedExerciseJournal;
import com.ogjg.daitgym.domain.feed.FeedExerciseJournalCollection;
import com.ogjg.daitgym.domain.journal.ExerciseJournal;
import com.ogjg.daitgym.domain.journal.ExerciseList;
import com.ogjg.daitgym.feed.dto.request.FeedSearchConditionRequest;
import com.ogjg.daitgym.feed.dto.response.FeedDetailResponse;
import com.ogjg.daitgym.feed.dto.response.FeedExerciseJournalCountResponse;
import com.ogjg.daitgym.feed.dto.response.FeedExerciseJournalListDto;
import com.ogjg.daitgym.feed.dto.response.FeedExerciseJournalListResponse;
import com.ogjg.daitgym.feed.repository.FeedExerciseJournalCollectionRepository;
import com.ogjg.daitgym.feed.repository.FeedExerciseJournalRepository;
import com.ogjg.daitgym.journal.dto.response.UserJournalDetailResponse;
import com.ogjg.daitgym.journal.dto.response.dto.UserJournalDetailDto;
import com.ogjg.daitgym.journal.dto.response.dto.UserJournalDetailExerciseListDto;
import com.ogjg.daitgym.journal.repository.journal.ExerciseJournalRepository;
import com.ogjg.daitgym.journal.service.ExerciseJournalHelper;
import com.ogjg.daitgym.like.feedExerciseJournal.repository.FeedExerciseJournalLikeRepository;
import com.ogjg.daitgym.user.service.UserHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedExerciseJournalService {

    private final FeedExerciseJournalRepository feedExerciseJournalRepository;
    private final ExerciseJournalRepository exerciseJournalRepository;
    private final FeedExerciseJournalLikeRepository feedExerciseJournalLikeRepository;
    private final FeedExerciseJournalCollectionRepository feedExerciseJournalCollectionRepository;
    private final FeedJournalHelper feedJournalHelper;
    private final UserHelper userHelper;
    private final ExerciseJournalHelper exerciseJournalHelper;

    /**
     * 전체 피드 운동일지 가져오기 목록보기 무한 스크롤
     * 분할 및 부위 분할 및 부위로 검색가능
     * todo 개선하기
     */
    @Transactional(readOnly = true)
    public FeedExerciseJournalListResponse feedExerciseJournalLists(
            Pageable pageable, FeedSearchConditionRequest feedSearchConditionRequest
    ) {
        Page<Long> feedExerciseJournals =
                feedExerciseJournalRepository.feedExerciseJournalLists(pageable, feedSearchConditionRequest);

        List<FeedExerciseJournalListDto> content =
                feedJournalHelper.feedExerciseJournalsChangeFeedExerciseJournalsDto(feedExerciseJournals);

        int totalpage = feedExerciseJournals.getTotalPages();
        if (!content.isEmpty()) totalpage -= 1;

        return new FeedExerciseJournalListResponse(totalpage, content);
    }

    /**
     * 팔로우 피드 운동일지 가져오기 목록보기 무한 스크롤
     * 분할 및 부위 분할 및 부위로 검색가능
     */
    @Transactional(readOnly = true)
    public FeedExerciseJournalListResponse followFeedJournalLists(
            String email, Pageable pageable, FeedSearchConditionRequest feedSearchConditionRequest
    ) {
        Page<Long> feedExerciseJournals =
                feedExerciseJournalRepository.feedExerciseJournalListsByFollow(email, pageable, feedSearchConditionRequest);

        List<FeedExerciseJournalListDto> content =
                feedJournalHelper.feedExerciseJournalsChangeFeedExerciseJournalsDto(feedExerciseJournals);

        int totalpage = feedExerciseJournals.getTotalPages();

        if (!content.isEmpty()) totalpage -= 1;

        return new FeedExerciseJournalListResponse(totalpage, content);
    }

    /**
     * 피드 운동일지 스크랩
     */
    @Transactional
    public void feedExerciseJournalScrap(String email, Long feedExerciseJournalId) {
        if (feedJournalHelper.feedExerciseJournalCollectionScrapStatus(email, feedExerciseJournalId))
            throw new AlreadyExistFeedJournalCollection();

        feedExerciseJournalCollectionRepository.save(
                new FeedExerciseJournalCollection(
                        userHelper.findUserByEmail(email),
                        feedJournalHelper.findFeedJournalById(feedExerciseJournalId)
                )
        );
    }

    /**
     * 피드 운동일지 스크랩 취소
     */
    @Transactional
    public void feedExerciseJournalDeleteScrap(String email, Long feedExerciseJournalId) {
        feedExerciseJournalCollectionRepository.delete(
                feedJournalHelper.findFeedExerciseJournalCollectionByUserAndFeedExerciseJournal(email, feedExerciseJournalId)
        );
    }

    /**
     * 피드 운동일지 삭제하기
     * 피드 좋아요 삭제
     * 피드 댓글 삭제
     * 피드 이미지 삭제
     * 운동일지 공개여부 false로 변경
     */
    @Transactional
    public void deleteFeedJournal(String email, Long feedJournalId) {
        feedJournalHelper.deleteFeedJournal(email, feedJournalId);
    }

    /**
     * 운동일지 수 조회
     */
    @Transactional(readOnly = true)
    public FeedExerciseJournalCountResponse countExerciseJournal(String nickname) {
        User user = feedJournalHelper.findUserByNickname(nickname);

        return new FeedExerciseJournalCountResponse(
                exerciseJournalRepository.countByUserAndIsCompleted(user, true)
        );
    }

    /**
     * 피드 운동일지 피드부분 상세정보 가져오기
     */
    @Transactional(readOnly = true)
    public FeedDetailResponse feedDetail(Long feedJournalId, String email) {
        FeedDetailResponse feedDetail = feedExerciseJournalRepository.feedDetail(feedJournalId)
                .orElseThrow(NotFoundFeedJournal::new);

        feedDetail.setFeedDetails(
                feedExerciseJournalLikeRepository.existsByUserEmailAndFeedExerciseJournalId(email, feedJournalId),
                feedJournalHelper.feedExerciseJournalCollectionScrapStatus(email, feedJournalId),
                feedJournalHelper.feedExerciseJournalLikes(feedJournalId),
                feedJournalHelper.feedExerciseJournalScrapCounts(feedJournalId),
                feedJournalHelper.feedImageListsDto(feedJournalHelper.findFeedJournalById(feedJournalId))
        );

        return feedDetail;
    }

    /**
     * 피드 운동일지 운동일지 부분 상세보기
     * 공개여부 확인후 운동일지 반환
     */
    @Transactional(readOnly = true)
    public UserJournalDetailResponse JournalDetail(Long feedJournalId) {
        FeedExerciseJournal feedJournal = feedJournalHelper.findFeedJournalById(feedJournalId);
        Long exerciseJournalId = feedJournal.getExerciseJournal().getId();
        exerciseJournalHelper.checkExerciseJournalDisclosure(exerciseJournalId);
        ExerciseJournal exerciseJournal = exerciseJournalHelper.findExerciseJournal(exerciseJournalId);

        List<ExerciseList> journalList = exerciseJournalHelper.findExerciseLists(exerciseJournal);
        List<UserJournalDetailExerciseListDto> exerciseListsDto = exerciseJournalHelper.exerciseListsChangeUserJournalDetailsDto(journalList);
        UserJournalDetailDto userJournalDetailDto = new UserJournalDetailDto(exerciseJournal, exerciseListsDto);

        return new UserJournalDetailResponse(userJournalDetailDto);
    }

}
