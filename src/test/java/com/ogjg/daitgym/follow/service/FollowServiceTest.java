package com.ogjg.daitgym.follow.service;

import com.ogjg.daitgym.domain.User;
import com.ogjg.daitgym.domain.follow.Follow;
import com.ogjg.daitgym.follow.dto.response.FollowListResponse;
import com.ogjg.daitgym.follow.repository.FollowRepository;
import com.ogjg.daitgym.user.repository.InbodyRepository;
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

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class FollowServiceTest {

    @Autowired
    FollowRepository followRepository;

    @Autowired
    FollowService followService;

    @Autowired
    UserHelper userHelper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InbodyRepository inbodyRepository;

    Follow.PK followPk;

    @BeforeEach
    void beforeTest() {
        followPk = Follow.createFollowPK(
                "targetEmail@test.email", "followerEamil@test.email");
        userRepository.save(
                User.builder().email("targetEmail@test.email").nickname("targetNickname").build());
        userRepository.save(
                User.builder().email("followerEmail@test.email").nickname("followerNickname").build());
    }

    @Test
    @DisplayName("팔로우")
    @Transactional
    public void follow() {
        followService.follow("followerEmail@test.email", "targetNickname");

        List<Follow> followList = followRepository.findAll();
        assertThat(followList.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("언팔로우")
    @Transactional
    public void unfollow() {
        followService.follow("followerEmail@test.email", "targetNickname");
        followService.unfollow("followerEmail@test.email", "targetNickname");

        assertThat(followRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("내가 팔로우한 사람들 목록")
    @Transactional
    public void followingList() {
        IntStream.range(1, 5).forEach(i ->
                userRepository.save(User.builder().email("targetEmail" + i + "@test.email").nickname("targetNickname" + i).build())
        );

        IntStream.range(1, 5).forEach(i ->
                followRepository.save(new Follow(
                        new Follow.PK("targetEmail" + i + "@test.email", "followerEamil@test.email"),
                        userHelper.findUserByEmail("targetEmail" + i + "@test.email"),
                        userHelper.findUserByEmail("followerEmail@test.email")
                ))
        );

        FollowListResponse followingList = followService.followingList("followerNickname");

        assertThat(followingList.getFollowList().size()).isEqualTo(4);
        assertThat(followingList.getFollowList().get(0).getNickname()).isEqualTo("targetNickname1");
        assertThat(followingList.getFollowList().get(1).getNickname()).isEqualTo("targetNickname2");
        assertThat(followingList.getFollowList().get(2).getNickname()).isEqualTo("targetNickname3");
        assertThat(followingList.getFollowList().get(3).getNickname()).isEqualTo("targetNickname4");
    }

    @Test
    @DisplayName("나를 팔로우한 사람들 목록")
    @Transactional
    public void followerList() {
        IntStream.range(1, 5).forEach(i ->
                userRepository.save(User.builder().email("targetEmail" + i + "@test.email").nickname("targetNickname" + i).build())
        );

        IntStream.range(1, 5).forEach(i ->
                followRepository.save(new Follow(
                        new Follow.PK("followerEamil@test.email","targetEmail" + i + "@test.email"),
                        userHelper.findUserByEmail("followerEmail@test.email"),
                        userHelper.findUserByEmail("targetEmail" + i + "@test.email")
                ))
        );

        FollowListResponse followingList = followService.followerList("followerNickname");

        assertThat(followingList.getFollowList().size()).isEqualTo(4);
        assertThat(followingList.getFollowList().get(0).getNickname()).isEqualTo("targetNickname1");
        assertThat(followingList.getFollowList().get(1).getNickname()).isEqualTo("targetNickname2");
        assertThat(followingList.getFollowList().get(2).getNickname()).isEqualTo("targetNickname3");
        assertThat(followingList.getFollowList().get(3).getNickname()).isEqualTo("targetNickname4");
    }

    @Test
    @Transactional
    @DisplayName("팔로우 검색")
    public void findFollow() throws Exception {
        followRepository.save(new Follow(
                followPk,
                userHelper.findUserByEmail("targetEmail@test.email"),
                userHelper.findUserByEmail("followerEmail@test.email")
        ));

        FollowService reflectionClass = new FollowService(
                followRepository, inbodyRepository, userHelper);

        Follow findFollow = ReflectionTestUtils.invokeMethod(
                reflectionClass, "findFollowByFollowPK", followPk);

        assertThat(findFollow.getFollowPK().getFollowerEmail())
                .isEqualTo(followPk.getFollowerEmail());
    }

    @Test
    @DisplayName("유저의 가장최근 인바디점수 가져오기 없으면 0을 반환")
    @Transactional
    public void userLatestInBodyScore() {
        FollowService reflectionClass = new FollowService(
                followRepository, inbodyRepository, userHelper);

        Integer inBodyScore = ReflectionTestUtils.invokeMethod(
                reflectionClass, "userLatestInbodyScore", "targetEmail@test.email");

        assertThat(inBodyScore).isEqualTo(0);
    }
}