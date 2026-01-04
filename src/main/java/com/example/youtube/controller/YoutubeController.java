package com.example.youtube.controller;

import com.example.youtube.dto.YoutubeDto;
import com.example.youtube.dto.request.ChannelRequest;
import com.example.youtube.dto.request.DownloadRequest;
import com.example.youtube.service.YoutubeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
public class YoutubeController {
    private final YoutubeService youtubeService;

    @Value("${yt.container.name}")
    private String containerName;

    @Value("${yt.video.host-folder}")
    private String hostVideoFolder;

    @Value("${yt.video.container-folder}")
    private String containerVideoFolder;

    @PostMapping("/suggestion")
    public ResponseEntity<YoutubeDto> suggestionUrl(@RequestBody ChannelRequest channelRequest) throws BadRequestException {
        YoutubeDto result = youtubeService.suggestionUrl(channelRequest.getChannelName());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/download-video")
    public void downloadVideo(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String videoUrl = body.get("url");
        if (videoUrl == null || videoUrl.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            // Tạo tên file động
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = "video_" + timestamp + ".mp4";
            String containerPath = containerVideoFolder + "/" + fileName;
            String hostPath = hostVideoFolder + "/" + fileName;

            // Command yt-dlp
            String[] cmd = {
                    "docker", "exec", containerName,
                    "yt-dlp",
                    "-f", "bestvideo+bestaudio/best",
                    "--merge-output-format", "mp4",
                    "-o", containerPath,
                    videoUrl
            };

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Log tiến trình
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("yt-dlp failed with exit code " + exitCode);
                return;
            }

            // Check file tồn tại + size > 0
            File videoFile = new File(hostPath);
            int retries = 0;
            while ((!videoFile.exists() || videoFile.length() == 0) && retries < 50) {
                Thread.sleep(200);
                retries++;
            }
            if (!videoFile.exists() || videoFile.length() == 0) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("File not found or empty after download");
                return;
            }

            // Set header để Chrome hiện nút download xanh
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + videoFile.getName() + "\"");
            response.setHeader("Content-Length", String.valueOf(videoFile.length()));
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition, Content-Length");

            // Stream file về FE
            try (InputStream in = new FileInputStream(videoFile);
                 OutputStream out = response.getOutputStream()) {

                byte[] buffer = new byte[8192];
                int n;
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                }
                out.flush();
            }

            System.out.println("Video streamed successfully: " + videoFile.getName());
            videoFile.delete();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/download-thumbnail")
    public ResponseEntity<byte[]> downloadThumbnail(@RequestBody DownloadRequest req) {
        try {

            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = "thumbnails_" + timestamp + ".jpg";

            // Mở stream và đọc byte[]
            InputStream in = new URL(req.getUrl()).openStream();
            byte[] bytes = in.readAllBytes();
            in.close();

            // Trả về client
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setAccessControlExposeHeaders(Collections.singletonList("Content-Disposition, Content-Length"));

            return ResponseEntity.ok().headers(headers).body(bytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
