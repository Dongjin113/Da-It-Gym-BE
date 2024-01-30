package com.ogjg.daitgym.journal.repository.redis;

import com.ogjg.daitgym.journal.dto.response.UserJournalDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, UserJournalDetailResponse> redisTemplate;

    private final static String USER_JOURNAL_DETAILS = "userJournalDetail:";

    /**
     * redis에서 key 값으로 data찾아오기
     */
    public UserJournalDetailResponse getJournalDetailsByRedis(Long journalId) {
        return redisTemplate.opsForValue().get(USER_JOURNAL_DETAILS + journalId);
    }

    /**
     * redis에 key 값으로 value 저장하기
     */
    public void saveJournalDetailsForRedis(Long journalId, UserJournalDetailResponse value) {
        redisTemplate.opsForValue().set(USER_JOURNAL_DETAILS + journalId, value);
    }
}
