package com.example.support_chat_ai;

import javax.sound.sampled.*;
import java.io.File;

public class VoiceRecorder {

    private TargetDataLine microphone;
    private Thread recordingThread;

    private final File audioFile =
            new File("voice.wav");

    public void startRecording() {

        try {
            AudioFormat format = new AudioFormat(
                    16000,
                    16,
                    1,
                    true,
                    false
            );

            DataLine.Info info =
                    new DataLine.Info(
                            TargetDataLine.class,
                            format
                    );

            microphone =
                    (TargetDataLine) AudioSystem.getLine(info);

            microphone.open(format);
            microphone.start();

            System.out.println("🎤 RECORDING STARTED");

            recordingThread = new Thread(() -> {

                try {
                    AudioInputStream audioStream =
                            new AudioInputStream(microphone);

                    AudioSystem.write(
                            audioStream,
                            AudioFileFormat.Type.WAVE,
                            audioFile
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

            recordingThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File stopRecording() {

        if (microphone != null) {

            microphone.stop();
            microphone.close();

            System.out.println("⏹ RECORDING STOPPED");
            System.out.println(
                    "Audio saved: "
                    + audioFile.getAbsolutePath()
            );
        }

        return audioFile;
    }

}