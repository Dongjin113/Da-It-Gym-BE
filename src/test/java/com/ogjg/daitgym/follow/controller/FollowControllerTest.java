package com.ogjg.daitgym.follow.controller;

import com.ogjg.daitgym.common.ControllerTest;
import com.ogjg.daitgym.follow.dto.response.FollowCountResponse;
import com.ogjg.daitgym.follow.dto.response.FollowListDto;
import com.ogjg.daitgym.follow.dto.response.FollowListResponse;
import com.ogjg.daitgym.follow.repository.FollowRepository;
import com.ogjg.daitgym.follow.service.FollowService;
import com.ogjg.daitgym.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
class FollowControllerTest extends ControllerTest {

    @MockBean
    private FollowRepository followRepository;

    @MockBean
    private FollowService followService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("팔로우")
    public void follow() throws Exception {
        mockMvc.perform(post("/api/follows/{nickname}", "targetNickname"))
                .andExpect(jsonPath("$.status.code").value("200"))
                .andExpect(jsonPath("$.status.message").value("OK"))
                .andDo(document("follow",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("nickname").description("팔로우 당하는 사람의 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("status.code").description("응답 코드"),
                                fieldWithPath("status.message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터 현재는 null")
                        )))
                .andDo(print());
    }

    @Test
    @DisplayName("언팔로우")
    public void unfollow() throws Exception {
        mockMvc.perform(delete("/api/follows/{nickname}", "targetNickname"))
                .andExpect(jsonPath("$.status.code").value("200"))
                .andExpect(jsonPath("$.status.message").value("OK"))
                .andDo(document("unfollow",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("nickname").description("팔로우 취소 당하는 사람의 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("status.code").description("응답 코드"),
                                fieldWithPath("status.message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터 현재는 null")
                        )))
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 유저가 팔로우중인 유저 수")
    public void followingCounts() throws Exception {
        when(followService.followingCount(Mockito.anyString())).thenReturn(new FollowCountResponse(1));

        mockMvc.perform(get("/api/follows/following-counts/{nickname}", "targetNickname"))
                .andExpect(jsonPath("$.status.code").value("200"))
                .andExpect(jsonPath("$.status.message").value("OK"))
                .andDo(document("followingCounts",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("nickname").description("대상이 되는 유저 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("status.code").description("응답 코드"),
                                fieldWithPath("status.message").description("응답 메시지"),
                                fieldWithPath("data.followCounts").description("닉네임 사용자가 팔로우한 유저 수")
                        )
                )).andDo(print());
    }

    @Test
    @DisplayName("닉네임 유저를 팔로우한 유저 수")
    public void followerCounts() throws Exception {
        when(followService.followerCount(Mockito.anyString())).thenReturn(new FollowCountResponse(1));

        mockMvc.perform(get("/api/follows/follower-counts/{nickname}", "targetNickname"))
                .andExpect(jsonPath("$.status.code").value("200"))
                .andExpect(jsonPath("$.status.message").value("OK"))
                .andDo(document("followerCounts",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("nickname").description("대상이 되는 유저 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("status.code").description("응답 코드"),
                                fieldWithPath("status.message").description("응답 메시지"),
                                fieldWithPath("data.followCounts").description("닉네임 사용자가 팔로우한 유저 수")
                        )
                )).andDo(print());

    }


    @Test
    @DisplayName("닉네임 유저를 팔로우한 친구들 목록 가져오기")
    public void followerList() throws Exception {
        List<FollowListDto> followListDto = List.of(
                new FollowListDto("둘리1.jpg", "둘리1", "저는 둘리1입니다"),
                new FollowListDto("둘리2.jpg", "둘리2", "저는 둘리2입니다")
        );
        when(followService.followerList(anyString())).thenReturn(new FollowListResponse(followListDto));

        mockMvc.perform(get("/api/follows/follower-list/{nickname}", "targetNickname"))
                .andExpect(jsonPath("$.status.code").value("200"))
                .andExpect(jsonPath("$.status.message").value("OK"))
                .andDo(document("follower-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("nickname").description("대상이 되는 유저 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("status.code").description("응답 코드"),
                                fieldWithPath("status.message").description("응답 메시지"),
                                fieldWithPath("data.followList[].imageUrl").description("팔로워 이미지"),
                                fieldWithPath("data.followList[].nickname").description("팔로워 닉네임"),
                                fieldWithPath("data.followList[].intro").description("팔로워 소개"),
                                fieldWithPath("data.followList[].score").description("팔로워 인바디점수 인바디 점수가 없으면 0점으로 표시")
                        )
                )).andDo(print());
    }

    @Test
    @DisplayName("닉네임 유저가 팔로우한 친구들 목록 가져오기")
    public void followingList() throws Exception {
        List<FollowListDto> followListDto = List.of(
                new FollowListDto("둘리1.jpg", "둘리1", "저는 둘리1입니다"),
                new FollowListDto("둘리2.jpg", "둘리2", "저는 둘리2입니다")
        );
        when(followService.followingList(anyString())).thenReturn(new FollowListResponse(followListDto));

        mockMvc.perform(get("/api/follows/following-list/{nickname}", "targetNickname"))
                .andExpect(jsonPath("$.status.code").value("200"))
                .andExpect(jsonPath("$.status.message").value("OK"))
                .andDo(document("following-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("nickname").description("대상이 되는 유저 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("status.code").description("응답 코드"),
                                fieldWithPath("status.message").description("응답 메시지"),
                                fieldWithPath("data.followList[].imageUrl").description("팔로워 이미지"),
                                fieldWithPath("data.followList[].nickname").description("팔로워 닉네임"),
                                fieldWithPath("data.followList[].intro").description("팔로워 소개"),
                                fieldWithPath("data.followList[].score").description("팔로워 인바디점수 인바디 점수가 없으면 0점으로 표시")
                        )
                )).andDo(print());
    }
}