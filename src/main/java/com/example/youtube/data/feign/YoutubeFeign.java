package com.example.youtube.data.feign;

import com.example.youtube.dto.response.ChannelResponse;
import com.example.youtube.dto.response.VideoResponse;
import com.example.youtube.dto.response.YouTubeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "youtube-api", url = "https://www.googleapis.com/youtube/v3")
public interface YoutubeFeign {

    @GetMapping("/channels")
    ChannelResponse getChannelInfo(@RequestParam("part") String part,
                                   @RequestParam("forHandle") String forHandle,
                                   @RequestParam("key") String key);

    default ChannelResponse getChannelInfoWithChannelId(String part, String forHandle, String key) {
        return getChannelInfo(part, forHandle, key);
    }

    @GetMapping("/search")
    YouTubeResponse getAllVideo(@RequestParam("part") String part,
                                @RequestParam("channelId") String channelId,
                                @RequestParam("key") String key,
                                @RequestParam("type") String type,
                                @RequestParam("maxResults") Integer maxResults,
                                @RequestParam("order") String order);

    default YouTubeResponse getAllVideoWithChannelId(String part, String channelId, String key, String type,
                                                Integer maxResults, String order) {
        return getAllVideo(part, channelId, key, type, maxResults, order);
    }

    @GetMapping("/videos")
    VideoResponse getVideoDetail(
            @RequestParam("part") String part,
            @RequestParam("id") String videoId,
            @RequestParam("key") String apiKey
    );
}
