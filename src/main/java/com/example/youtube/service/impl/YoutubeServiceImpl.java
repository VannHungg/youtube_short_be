package com.example.youtube.service.impl;

import com.example.youtube.data.entity.YoutubeEntity;
import com.example.youtube.data.repository.YoutubeRepository;
import com.example.youtube.dto.YoutubeDto;
import com.example.youtube.dto.response.VideoResponse;
import com.example.youtube.dto.response.YouTubeResponse;
import com.example.youtube.service.ThirdPartyService;
import com.example.youtube.service.YoutubeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeServiceImpl implements YoutubeService {
    private final ThirdPartyService thirdPartyService;
    private final YoutubeRepository youtubeRepository;

    private static String PRE_URL_SHORT = "https://www.youtube.com/shorts/";
    private static int TOTAL_RECORD = 50;

    @Transactional
    @Override
    public YoutubeDto suggestionUrl(String channelName) throws BadRequestException {
        try {
            // get all video we have seen
            String channelId = thirdPartyService.getChannelIdByChannelName(channelName);

            Set<YoutubeEntity> allVideoHaveSeens = youtubeRepository.findAllByChannelId(channelId);
            Set<String> allVideoUrlHaveSeens = allVideoHaveSeens.stream().map(YoutubeEntity::getVideoId)
                    .collect(Collectors.toSet());

            int totalRecord = TOTAL_RECORD;
            Set<String> allVideos;
            while (true) {
                allVideos = thirdPartyService.getAllVideoIdOfChannel(channelId, totalRecord);
                allVideos.removeAll(allVideoUrlHaveSeens);

                if (!CollectionUtils.isEmpty(allVideos)) {
                    break;
                }
                totalRecord += TOTAL_RECORD;
            }
            List<String> listAllVideos = new ArrayList<>(allVideos);
            Random random = new Random();
            String videoId = listAllVideos.get(random.nextInt(listAllVideos.size()));

            YoutubeEntity youtube = YoutubeEntity.builder()
                    .channelId(channelId)
                    .channelName(channelName)
                    .videoId(videoId)
                    .lastSeen(new Date())
                    .build();
            youtubeRepository.save(youtube);

            VideoResponse videoResponse = thirdPartyService.getAllInfoOfVideo(videoId);
            VideoResponse.Item itemOfVideo = videoResponse.getItems().get(0);
            YoutubeDto result = YoutubeDto.builder()
                    .title(itemOfVideo.getSnippet().getTitle())
                    .description(itemOfVideo.getSnippet().getDescription())
                    .thumbnailsUrl(itemOfVideo.getSnippet().getThumbnails().getMaxres().getUrl())
                    .url(PRE_URL_SHORT + videoId)
                    .build();
            return result;
        }
        catch (Exception e) {
            log.error("Has error", e);
            throw e;
        }
    }

    @Override
    public String extractVideoId(String url) {
        if (url.contains("/shorts/")) {
            return url.substring(url.lastIndexOf("/shorts/") + 8);
        } else if (url.contains("watch?v=")) {
            return url.substring(url.indexOf("watch?v=") + 8);
        } else {
            return url;
        }
    }
}
