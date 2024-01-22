package com.ogjg.daitgym.follow.service;

import com.ogjg.daitgym.common.exception.follow.AlreadyFollowUser;
import com.ogjg.daitgym.common.exception.follow.NotFoundFollow;
import com.ogjg.daitgym.domain.Inbody;
import com.ogjg.daitgym.domain.User;
import com.ogjg.daitgym.domain.follow.Follow;
import com.ogjg.daitgym.follow.dto.response.FollowCountResponse;
import com.ogjg.daitgym.follow.dto.response.FollowListDto;
import com.ogjg.daitgym.follow.dto.response.FollowListResponse;
import com.ogjg.daitgym.follow.repository.FollowRepository;
import com.ogjg.daitgym.user.repository.InbodyRepository;
import com.ogjg.daitgym.user.service.UserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final InbodyRepository inbodyRepository;
    private final UserHelper userHelper;

    /**
     * 팔로우
     */
    @Transactional
    public void follow(String email, String targetNickname) {
        User user = userHelper.findUserByEmail(email);
        User targetUser = userHelper.findUserByNickname(targetNickname);

        if (user.getEmail().equals(targetUser.getEmail())) {
            throw new AlreadyFollowUser("자신은 팔로우 할 수 없습니다");
        }

        Follow.PK followPK = Follow.createFollowPK(targetUser.getEmail(), email);

        if (followRepository.findById(followPK).isPresent())
            throw new AlreadyFollowUser();

        followRepository.save(
                new Follow(followPK, targetUser, user)
        );
    }

    /**
     * 언팔로우
     */
    @Transactional
    public void unfollow(String email, String targetNickname) {
        User targetUser = userHelper.findUserByNickname(targetNickname);
        Follow.PK followPK = Follow.createFollowPK(targetUser.getEmail(), email);
        findFollowByFollowPK(followPK);
        followRepository.deleteById(followPK);
    }

    /**
     * 나를 팔로우 하고있는 사람의 수
     */
    @Transactional(readOnly = true)
    public FollowCountResponse followerCount(String nickname) {
        User user = userHelper.findUserByNickname(nickname);
        int followerCount = followRepository.countByFollowPKTargetEmail(user.getEmail());

        return new FollowCountResponse(followerCount);
    }

    /**
     * 내가 팔로우 하고있는 사람의 수
     */
    @Transactional(readOnly = true)
    public FollowCountResponse followingCount(String nickname) {
        User user = userHelper.findUserByNickname(nickname);
        int followingCount = followRepository.countByFollowPKFollowerEmail(user.getEmail());

        return new FollowCountResponse(followingCount);
    }

    /**
     * 내가 팔로우한 사람들 목록
     */
    @Transactional(readOnly = true)
    public FollowListResponse followingList(String nickname) {
        User user = userHelper.findUserByNickname(nickname);
        List<FollowListDto> followingList = followRepository.followingList(nickname);

        followingList.forEach(
                following -> following.putLatestInbodyScore(
                        userLatestInbodyScore(user.getEmail())
                )
        );

        return new FollowListResponse(followingList);
    }

    /**
     * 나를 팔로우한 사람들 목록
     */
    @Transactional(readOnly = true)
    public FollowListResponse followerList(String nickname) {
        User user = userHelper.findUserByNickname(nickname);
        List<FollowListDto> followerList = followRepository.followerList(nickname);

        followerList.forEach(
                follower -> follower.putLatestInbodyScore(
                        userLatestInbodyScore(user.getEmail())
                )
        );

        return new FollowListResponse(followerList);
    }

    /**
     * 가장 최근의 인바디
     * @param email
     * @return
     */
    private int userLatestInbodyScore(String email) {
        return inbodyRepository.findFirstByUserEmailOrderByCreatedAtDesc(email)
                .map(Inbody::getScore)
                .orElse(0);
    }

    /**
     * Follow 찾기
     */
    private Follow findFollowByFollowPK(
            Follow.PK followPk
    ) {
        return followRepository.findById(followPk)
                .orElseThrow(NotFoundFollow::new);
    }
}
