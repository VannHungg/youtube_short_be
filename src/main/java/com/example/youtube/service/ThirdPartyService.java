package com.example.youtube.service;

import com.example.youtube.dto.response.VideoResponse;
import com.example.youtube.dto.response.YouTubeResponse;
import org.apache.coyote.BadRequestException;

import java.util.Set;

public interface ThirdPartyService {

    String getChannelIdByChannelName(String channelName) throws BadRequestException;

    Set<String> getAllVideoIdOfChannel(String channelId, int totalRecord);

    VideoResponse getAllInfoOfVideo(String videoId) throws BadRequestException;
}
