package com.example.youtube.service.impl;

import com.example.youtube.data.feign.YoutubeFeign;
import com.example.youtube.dto.response.ChannelResponse;
import com.example.youtube.dto.response.VideoResponse;
import com.example.youtube.dto.response.YouTubeResponse;
import com.example.youtube.service.ThirdPartyService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ThirdPartyServiceImpl implements ThirdPartyService {
    @Value("${youtube.api.key}")
    private String youtubeKey;

    private final YoutubeFeign youtubeFeign;

    @Override
    public String getChannelIdByChannelName(String channelName) throws BadRequestException {
        ChannelResponse channel = youtubeFeign.getChannelInfoWithChannelId("id", channelName, youtubeKey);
        if (ObjectUtils.isEmpty(channel.getItems())) {
            throw new BadRequestException("Channel not found");
        }

        return channel.getItems().get(0).getId();
    }

    @Override
    public Set<String> getAllVideoIdOfChannel(String channelId, int totalRecord) {
        YouTubeResponse youtube = youtubeFeign.getAllVideoWithChannelId(
                "id", channelId, youtubeKey,"video",totalRecord, "date");
        return youtube.getItems()
                .stream().map(YouTubeResponse.YoutubeItemResponse::getId)
                .map(YouTubeResponse.SubItemResponse::getVideoId)
                .collect(Collectors.toSet());
    }

    @Override
    public VideoResponse getAllInfoOfVideo(String videoId) throws BadRequestException {
        VideoResponse video = youtubeFeign.getVideoDetail("snippet", videoId, youtubeKey);
        if (ObjectUtils.isEmpty(video)) {
            throw new BadRequestException("Video not found");
        }
        return video;
    }
}
