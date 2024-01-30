package com.ogjg.daitgym.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.ogjg.daitgym.chat.dto.ChatMessageDto;
import com.ogjg.daitgym.exercise.dto.response.ExerciseListResponse;
import com.ogjg.daitgym.journal.dto.response.UserJournalDetailResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.LocalDateTime;

@Configuration
public class RedisConfig {
    /**
     * redis 연결, redis 의 pub/sub 기능을 이용하기 위해 pub/sub 메시지를 처리하는 MessageListener 설정(등록)
     * RedisConnectionFactory : Redis 서버와의 연결을 생성하고 관리하는 데 사용
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    /**
     * Redis 데이터베이스와의 상호작용을 위한 RedisTemplate 을 설정. JSON 형식으로 담기 위해 직렬화
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, ExerciseListResponse> redisTemplateExercise(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ExerciseListResponse> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ExerciseListResponse.class));
        return redisTemplate;
    }


//    @Bean
//    public <T> RedisTemplate<String, T> redisTemplate(RedisConnectionFactory connectionFactory, Class<T> tClass) {
//        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(connectionFactory);
//
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(tClass));
//        return redisTemplate;
//    }

    /**
     * Redis 에 메시지 내역을 저장하기 위한 RedisTemplate 을 설정
     */
    @Bean
    public RedisTemplate<String, ChatMessageDto> redisTemplateMessage(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ChatMessageDto> redisTemplateMessage = new RedisTemplate<>();
        redisTemplateMessage.setConnectionFactory(connectionFactory);
        redisTemplateMessage.setKeySerializer(new StringRedisSerializer());
        redisTemplateMessage.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessageDto.class))
        ;

        return redisTemplateMessage;
    }

    @Bean
    public RedisTemplate<String, UserJournalDetailResponse> redisTemplateJournalDetail(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UserJournalDetailResponse> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // ObjectMapper 생성 및 JavaTimeModule 등록
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        javaTimeModule.addSerializer(LocalDateTimeSerializer.INSTANCE);
        javaTimeModule.addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
        objectMapper.registerModule(javaTimeModule);
        Jackson2JsonRedisSerializer<UserJournalDetailResponse> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, UserJournalDetailResponse.class);
        redisTemplate.setValueSerializer(jsonRedisSerializer);

        return redisTemplate;
    }

}