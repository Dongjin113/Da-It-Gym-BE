package com.ogjg.daitgym.exercise.service;

import com.ogjg.daitgym.common.exception.ErrorCode;
import com.ogjg.daitgym.common.exception.exercise.NotFoundExercise;
import com.ogjg.daitgym.domain.exercise.Exercise;
import com.ogjg.daitgym.domain.exercise.ExercisePart;
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
@Transactional
class ExerciseTest {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExercisePartRepository exercisePartRepository;

    @Autowired
    private ExerciseHelper exerciseHelper;

    private ExercisePart exercisePart;
    private Exercise exercise;

    @BeforeEach
    void beforeEachTest() {
        exercise = exerciseRepository.save(
                new Exercise("걷기"));
        exercisePart = exercisePartRepository.save(
                new ExercisePart(exercise, "유산소"));
    }

    @Test
    @DisplayName("운동이름으로 운동찾기")
    void findExerciseByName() {
        Exercise findExercise = exerciseHelper.findExercise("걷기");
        assertThat(findExercise.getName()).isEqualTo("걷기");
    }

    @Test
    @DisplayName("[Exception] 운동이 존재하지 않을때 운동이름으로 운동찾기")
    void findExerciseByNameThrowException() {
        assertThatThrownBy(() -> exerciseHelper.findExercise("걷기1"))
                .isInstanceOf(NotFoundExercise.class)
                .hasMessage(ErrorCode.NOT_FOUND_EXERCISE.getMessage());
    }

    @Test
    @DisplayName("운동ID로 운동찾기")
    void findExerciseById() {
        Exercise findExercise = exerciseHelper.findExercise(exercise.getId());
        assertThat(findExercise.getName()).isEqualTo("걷기");
    }

    @Test
    @DisplayName("[Exception] 운동이 존재하지 않을때 운동ID로 운동찾기")
    void findExerciseByIdThrowException() {
        assertThatThrownBy(() -> exerciseHelper.findExercise(-1L))
                .isInstanceOf(NotFoundExercise.class)
                .hasMessage(ErrorCode.NOT_FOUND_EXERCISE.getMessage());
    }

    @Test
    @DisplayName("운동으로 운동부위 찾기")
    void findExerciseByExercisePart() {
        assertThat(exerciseHelper.findExercisePartByExercise(exercise))
                .isEqualTo("유산소");
    }

    @Test
    @DisplayName("운동부위로 운동 찾기")
    void findExercisePartByExercise() {
        Exercise exercise1 = exerciseRepository.save(new Exercise("줄넘기"));
        exercisePartRepository.save(new ExercisePart(exercise1, "유산소"));

        List<ExercisePart> exerciseLists = exerciseHelper.findExerciseListsByPart(exercisePart.getPart());
        assertThat(exerciseLists).hasSize(2);
        assertThat(exerciseLists).extracting("exercise.name").contains("줄넘기");
        assertThat(exerciseLists).extracting("exercise.name").contains("걷기");
        assertThat(exerciseLists).hasSize(2)
                .extracting("exercise")
                .extracting("name")
                .containsExactly("걷기", "줄넘기");
    }
}