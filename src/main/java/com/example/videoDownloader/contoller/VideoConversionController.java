package com.example.videoDownloader.contoller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class VideoConversionController {

    @CrossOrigin(origins = "*")
    @PostMapping("/convert/toMp4")
    public String convertToMp4(@RequestParam("file") MultipartFile file) {
        try {
           
            Path tempDir = Files.createTempDirectory("uploads");
            Path tempFile = tempDir.resolve(file.getOriginalFilename());
            file.transferTo(tempFile);

            return convertVideoToMp4(tempFile.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "Error uploading file: " + e.getMessage();
        }
    }

    private String convertVideoToMp4(String inputFilePath) {
        try {
            String ffmpegPath = "C:\\ffmpeg\\bin\\ffmpeg.exe";
            String userHome = System.getProperty("user.home");
            String outputFilePath = userHome + "\\Downloads\\" + new File(inputFilePath).getName().replaceAll("\\..*$", ".mp4");

            System.out.println("ffmpeg path: " + ffmpegPath);
            System.out.println("Input file path: " + inputFilePath);
            System.out.println("Output file path: " + outputFilePath);

            ProcessBuilder builder = new ProcessBuilder(
                    ffmpegPath,
                    "-i", inputFilePath, 
                    "-c:v", "copy",
                    "-c:a", "copy", 
                    outputFilePath 
            );
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                System.out.println(line); 
                output.append(line).append("\n");
            }
            process.waitFor();

            int exitCode = process.exitValue();
            System.out.println("ffmpeg exit code: " + exitCode);
            System.out.println("ffmpeg output: " + output.toString());

            if (exitCode == 0) {
                return "Converted video successfully to " + outputFilePath + ":\n" + output.toString();
            } else {
                return "Error converting video: ffmpeg exited with code " + exitCode + "\n" + output.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error converting video: " + e.getMessage();
        }
    }
}
