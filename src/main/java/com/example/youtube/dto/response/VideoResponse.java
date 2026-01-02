package com.example.youtube.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponse {
    private List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String kind;
        private String etag;
        private String id;
        private Snippet snippet;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Snippet {
        private String publishedAt;
        private String channelId;
        private String title;
        private String description;
        private Thumbnails thumbnails;
        private String channelTitle;
        private String categoryId;
        private String liveBroadcastContent;
        private String defaultLanguage;
        private String defaultAudioLanguage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Thumbnails {
        @JsonProperty("default")
        private ThumbnailInfo defaultThumbnail;
        private ThumbnailInfo medium;
        private ThumbnailInfo high;
        private ThumbnailInfo standard;
        private ThumbnailInfo maxres;
    }

    @Data
    public static class ThumbnailInfo {
        private String url;
        private Integer width;
        private Integer height;
    }
}
