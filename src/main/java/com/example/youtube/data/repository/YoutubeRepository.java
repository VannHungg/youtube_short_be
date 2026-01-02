package com.example.youtube.data.repository;

import com.example.youtube.data.entity.YoutubeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface YoutubeRepository extends JpaRepository<YoutubeEntity, Long> {
    @Query("select y from YoutubeEntity y where y.channelId = :channelId")
    Set<YoutubeEntity> findAllByChannelId(@Param("channelId") String channelId);

}
