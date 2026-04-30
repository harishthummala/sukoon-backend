package com.sukoon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageEntryRepository
        extends JpaRepository<ChatMessageEntry, Long> {

    // Get messages in order for AI memory
    List<ChatMessageEntry> findByChatIdOrderByTimestampAsc(Long chatId);
    int countByChatId(Long chatId);

}