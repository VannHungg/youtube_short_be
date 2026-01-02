package com.example.youtube.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YouTubeResponse {
    private List<YoutubeItemResponse> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YoutubeItemResponse {
        private String kind;
        private String etag;
        private SubItemResponse id;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubItemResponse {
        private String kind;
        private String videoId;
    }
}
