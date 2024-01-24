package com.ogjg.daitgym.exercise.service;

import com.ogjg.daitgym.common.exception.ErrorCode;
import com.ogjg.daitgym.common.exception.exercise.NotFoundExercise;
import com.ogjg.daitgym.domain.exercise.Exercise;
import com.ogjg.daitgym.domain.exercise.ExercisePart;
import com.ogjg.daitgym.exercise.dto.response.ExerciseListResponse;
import com.ogjg.daitgym.exercise.repository.ExercisePartRepository;
import com.ogjg.daitgym.exercise.repository.ExerciseRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Slf4j
@SpringBootTest
class ExerciseTest {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExercisePartRepository exercisePartRepository;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private ExerciseHelper exerciseHelper;

    private ExercisePart exercisePart;
    private Exercise exercise;

    private final String exerciseName1 = "걷기";
    private final String exerciseName2 = "뛰기";
    private final String exercisePartName = "유산소";

    @BeforeEach
    @Transactional
    void beforeEachTest() {
        exercise = exerciseRepository.save(
                new Exercise(exerciseName1));
        exercisePart = exercisePartRepository.save(
                new ExercisePart(exercise, exercisePartName));
    }

    @Test
    @DisplayName("[운동 검색] 운동이름으로 운동찾기")
    @Transactional
    void findExerciseByName() {
        Exercise findExercise = exerciseHelper.findExercise(exerciseName1);
        assertThat(findExercise.getName()).isEqualTo(exerciseName1);
    }

    @Test
    @DisplayName("[Exception] 운동이 존재하지 않을때 운동이름으로 운동찾기")
    @Transactional
    void findExerciseByNameThrowException() {
        assertThatThrownBy(() -> exerciseHelper.findExercise("걷기1"))
                .isInstanceOf(NotFoundExercise.class)
                .hasMessage(ErrorCode.NOT_FOUND_EXERCISE.getMessage());
    }

    @Test
    @DisplayName("[운동 검색] 운동ID로 운동찾기")
    @Transactional
    void findExerciseById() {
        Exercise findExercise = exerciseHelper.findExercise(exercise.getId());
        assertThat(findExercise.getName()).isEqualTo(exerciseName1);
    }

    @Test
    @DisplayName("[Exception] 운동이 존재하지 않을때 운동ID로 운동찾기")
    @Transactional
    void findExerciseByIdThrowException() {
        assertThatThrownBy(() -> exerciseHelper.findExercise(-1L))
                .isInstanceOf(NotFoundExercise.class)
                .hasMessage(ErrorCode.NOT_FOUND_EXERCISE.getMessage());
    }

    @Test
    @DisplayName("[운동부위 검색] 운동으로 운동부위 찾기")
    @Transactional
    void findExerciseByExercisePart() {
        assertThat(exerciseHelper.findExercisePartByExercise(exercise))
                .isEqualTo(exercisePartName);
    }

    @Test
    @DisplayName("[부위별 운동 검색]운동부위로 운동 찾기")
    @Transactional
    void findExercisePartByExercise() {
        Exercise exercise1 = exerciseRepository.save(new Exercise("줄넘기"));
        exercisePartRepository.save(new ExercisePart(exercise1, exercisePartName));

        List<ExercisePart> exerciseLists = exerciseHelper.findExerciseListsByPart(exercisePart.getPart());
        assertThat(exerciseLists).hasSize(2);
        assertThat(exerciseLists).extracting("exercise.name").contains("줄넘기");
        assertThat(exerciseLists).extracting("exercise.name").contains(exerciseName1);
        assertThat(exerciseLists).hasSize(2)
                .extracting("exercise")
                .extracting("name")
                .containsExactly(exerciseName1, "줄넘기");
    }

    @Test
    @DisplayName("[부위별 운동 검색] 운동 부위명으로 부위별 운동정보 가져오기")
    @Transactional
    public void serviceTest() {
        Exercise exercise1 = exerciseRepository.save(
                new Exercise(exerciseName2));
        exercisePartRepository.save(
                new ExercisePart(exercise1, exercisePartName));
        Exercise exercise2 = exerciseRepository.save(
                new Exercise("푸시 업"));
        exercisePartRepository.save(
                new ExercisePart(exercise2, "가슴"));

        ExerciseListResponse exerciseList = exerciseService.exerciseLists(exercisePartName);

        assertThat(exerciseList.getExercises()).hasSize(2);
        assertThat(exerciseList.getExercises().get(0).getExerciseName()).isEqualTo(exerciseName1);
        assertThat(exerciseList.getExercises().get(0).getExercisePart()).isEqualTo(exercisePartName);
        assertThat(exerciseList.getExercises().get(1).getExerciseName()).isEqualTo(exerciseName2);
        assertThat(exerciseList.getExercises().get(1).getExercisePart()).isEqualTo(exercisePartName);
    }

    @Test
    @Transactional
    @DisplayName("[성능 테스트] Redis를 통한 조회")
    public void getExerciseListByRedis1() {
        ExerciseListResponse exerciseLists = exerciseService.redisExerciseLists(exercisePartName);
        assertThat(exerciseLists.getExercises().get(0).getExerciseName()).isEqualTo("걷기");
        assertThat(exerciseLists.getExercises().get(0).getExercisePart()).isEqualTo("유산소");
    }

    @Test
    @Transactional
    @DisplayName("[성능 테스트] DB를 통한 조회")
    public void getExerciseListByRedis2() {
        ExerciseListResponse exerciseLists = exerciseService.exerciseLists(exercisePartName);
        assertThat(exerciseLists.getExercises().get(0).getExerciseName()).isEqualTo("걷기");
        assertThat(exerciseLists.getExercises().get(0).getExercisePart()).isEqualTo("유산소");
    }
}