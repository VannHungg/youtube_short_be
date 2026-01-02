package com.example.youtube.service;

import com.example.youtube.dto.YoutubeDto;
import org.apache.coyote.BadRequestException;

public interface YoutubeService {
    YoutubeDto suggestionUrl(String channelName) throws BadRequestException;

    String extractVideoId(String url);
}
