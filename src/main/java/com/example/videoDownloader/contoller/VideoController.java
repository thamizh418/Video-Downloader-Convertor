package com.example.videoDownloader.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.videoDownloader.service.VideoService;

@RestController
public class VideoController {
	
	@Autowired
	VideoService videoService;

    @CrossOrigin(origins = "*")
    @GetMapping("/download/video")
    public String downloadVideo(@RequestParam String url, @RequestParam String format, @RequestParam String quality) {
    	System.err.println("Request -> "+url+"-"+format+"-"+quality);
        return videoService.downloadVideoFromPlatform(url, format, quality);
    }
}
