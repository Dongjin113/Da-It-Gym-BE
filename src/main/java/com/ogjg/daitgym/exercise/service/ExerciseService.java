package com.ogjg.daitgym.exercise.service;

import com.ogjg.daitgym.exercise.dto.response.ExerciseListDto;
import com.ogjg.daitgym.exercise.dto.response.ExerciseListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseHelper exerciseHelper;
    private final RedisTemplate<String, ExerciseListResponse> redisTemplate;

    /**
     * 운동부위에 속하는 운동 목록 반환
     */
    public ExerciseListResponse exerciseLists(
            String part
    ) {
        List<ExerciseListDto> exerciseListDtos = exerciseHelper.findExerciseListsByPart(part)
                .stream()
                .map(exercisePart -> new ExerciseListDto(
                        exercisePart.getExercise().getId(),
                        exercisePart.getExercise().getName(),
                        exercisePart.getPart())
                ).toList();

        return new ExerciseListResponse(exerciseListDtos);
    }

    /**
     * 운동부위에 속하는 운동 목록 반환
     */
    @Transactional
    public ExerciseListResponse redisExerciseLists(
            String part
    ) {
        String key = "exercisePartList:" + part;
        ExerciseListResponse exercisePartList = getExercisePartListByRedis(key);

        if (exercisePartList == null) {
            List<ExerciseListDto> exerciseListDtos = exerciseHelper.findExerciseListsByPart(part)
                    .stream()
                    .map(exercisePart -> new ExerciseListDto(
                            exercisePart.getExercise().getId(),
                            exercisePart.getExercise().getName(),
                            exercisePart.getPart())
                    ).toList();
            exercisePartList = new ExerciseListResponse(exerciseListDtos);
            saveExercisePartListToRedis(key, exercisePartList);
        }

        return exercisePartList;
    }

    /**
     * redis에서 key 값으로 data찾아오기
     */
    private ExerciseListResponse getExercisePartListByRedis(String key) {

        return redisTemplate.opsForValue().get(key);
    }

    /**
     * redis에 key 값으로 value 저장하기
     */
    private void saveExercisePartListToRedis(String key, ExerciseListResponse value) {
        redisTemplate.opsForValue().set(key, value);
    }
}