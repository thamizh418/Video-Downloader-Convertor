package com.example.videoDownloader.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class VideoService {
	
	 public String downloadVideoFromPlatform(String url, String format, String quality) {
	        try {
	            String ytDlpPath = "C:\\yt-dlp\\yt-dlp.exe"; 
	            String ffmpegDir = "C:\\ffmpeg\\bin"; 
	            String userHome = System.getProperty("user.home");

	            String qualityOption;
	            switch (quality.toLowerCase()) {
	                case "low":
	                    qualityOption = "best[height<=480]";
	                    break;
	                case "mid":
	                    qualityOption = "best[height<=720]";
	                    break;
	                case "high":
	                    qualityOption = "best[height<=1080]";
	                    break;
	                default:
	                    qualityOption = "best";
	            }

	            System.out.println("yt-dlp path: " + ytDlpPath);
	            System.out.println("ffmpeg path: " + ffmpegDir + "\\ffmpeg.exe");
	            System.out.println("Video URL: " + url);
	            System.out.println("Format: " + format);
	            System.out.println("Quality: " + quality);

	            String dateStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

	            String outputFilePathTemplate = userHome 
	            		+ "\\Downloads\\Videos\\%(title)s_" 
	            		+ quality + "_" 
	            		+ dateStr + ".%(ext)s";

	            ProcessBuilder listFormatsBuilder = new ProcessBuilder(ytDlpPath, "--list-formats", url);
	            
	            listFormatsBuilder.redirectErrorStream(true);
	            
	            Process listFormatsProcess = listFormatsBuilder.start();

	            BufferedReader listFormatsReader = new BufferedReader(new InputStreamReader(listFormatsProcess.getInputStream()));
	            
	            String line;
	            
	            List<String> availableFormats = new ArrayList<>();
	            
	            while ((line = listFormatsReader.readLine()) != null) {
	                System.out.println(line); 
	                availableFormats.add(line);
	            }
	            
	            listFormatsProcess.waitFor();

	            String selectedFormat = selectBestAvailableFormat(availableFormats, qualityOption);

	            ProcessBuilder builder;
	            if (format.equalsIgnoreCase("mp3")) {
	                builder = new ProcessBuilder(
	                        ytDlpPath, "--verbose",
	                        "-o", outputFilePathTemplate,
	                        "--extract-audio", "--audio-format", "mp3",
	                        "--ffmpeg-location", ffmpegDir,
	                        url);
	            } else {
	                builder = new ProcessBuilder(
	                        ytDlpPath, "--verbose",
	                        "-o", outputFilePathTemplate,
	                        "-f", selectedFormat,
	                        "--ffmpeg-location", ffmpegDir,
	                        url);
	            }

	            builder.environment().put("PATH", System.getenv("PATH") + ";" + ffmpegDir);
	            
	            builder.redirectErrorStream(true);
	            
	            Process process = builder.start();

	            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	            
	            StringBuilder output = new StringBuilder();
	            
	            while ((line = reader.readLine()) != null) {
	                System.out.println(line); 
	                output.append(line).append("\n");
	            }
	            
	            process.waitFor();

	            int exitCode = process.exitValue();
	            
	            System.out.println("yt-dlp exit code: " + exitCode);
	            System.out.println("yt-dlp output: " + output.toString());

	            if (exitCode != 0) {
	                throw new RuntimeException("yt-dlp exited with code " + exitCode + ": " + output.toString());
	            }

	            return "Downloaded video successfully to " + userHome + "\\Downloads:\n" + output.toString();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "Error downloading video: " + e.getMessage();
	        }
	    }
	 
	    private String selectBestAvailableFormat(List<String> availableFormats, String qualityOption) {
	        for (String format : availableFormats) {
	            if (format.contains(qualityOption)) {
	                return format.split(" ")[0];
	            }
	        }
	        return "best";
	    }

}
