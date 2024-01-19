package com.ogjg.daitgym.exercise.controller;

import com.ogjg.daitgym.common.ControllerTest;
import com.ogjg.daitgym.exercise.dto.response.ExerciseListDto;
import com.ogjg.daitgym.exercise.dto.response.ExerciseListResponse;
import com.ogjg.daitgym.exercise.repository.ExercisePartRepository;
import com.ogjg.daitgym.exercise.repository.ExerciseRepository;
import com.ogjg.daitgym.exercise.service.ExerciseHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@Transactional
class ExerciseControllerTest extends ControllerTest {

    @MockBean
    private ExerciseRepository exerciseRepository;

    @MockBean
    private ExercisePartRepository exercisePartRepository;

    @Autowired
    private ExerciseHelper exerciseHelper;

    @Test
    @DisplayName("운동부위로 부위별 운동목록 조회해오기")
    void exerciseList() throws Exception {
        final String part = "유산소";
        final String exerciseName1 = "걷기";
        final String exerciseName2 = "뛰기";

        ExerciseListResponse exerciseLists = new ExerciseListResponse(List.of(
                new ExerciseListDto(1L, exerciseName1, part),
                new ExerciseListDto(2L, exerciseName2, part)
        ));

        when(exerciseService.exerciseLists(part)).thenReturn(exerciseLists);

        mockMvc.perform(get("/api/exercises/{part}", part)
                        .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.status.code").value("200"))
                .andExpect(jsonPath("$.status.message").value("OK"))
                .andExpect(jsonPath("$.data.exercises[0].exerciseName").value(exerciseName1))
                .andExpect(jsonPath("$.data.exercises[1].exerciseName").value(exerciseName2))
                .andDo(document("part-exercises",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("part").description("운동 목록을 볼 운동 부위")
                        )
                        , responseFields(
                                fieldWithPath("status.code").description("응답 코드"),
                                fieldWithPath("status.message").description("응답 메시지"),

                                fieldWithPath("data.exercises[].exerciseId").description("운동 ID"),
                                fieldWithPath("data.exercises[].exerciseName").description("운동 이름"),
                                fieldWithPath("data.exercises[].exercisePart").description("운동 부위")
                        )));
    }
}