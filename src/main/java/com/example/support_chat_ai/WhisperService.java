package com.example.support_chat_ai;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Service
public class WhisperService {

    public String transcribe(File audioFile) {

        try {

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "py",
                    "-m",
                    "whisper",
                    audioFile.getAbsolutePath(),
                    "--model",
                    "base",
                    "--language",
                    "en"
            );

            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(process.getInputStream())
                    );

            String line;

            while ((line = reader.readLine()) != null) {

                System.out.println("WHISPER: " + line);

                if (line.contains("-->")) {
                    String[] parts = line.split("]", 2);

                    if (parts.length == 2) {
                        output.append(parts[1].trim()).append(" ");
                    }
                }
            }

            process.waitFor();

            return output.toString().trim();

        } catch (Exception e) {

            e.printStackTrace();

            return "Speech recognition failed";
        }
    }
}