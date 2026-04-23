package com.sukoon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    // find all mood entries for a specific user
    List<MoodEntry> findByUserId(Long userId);
}
