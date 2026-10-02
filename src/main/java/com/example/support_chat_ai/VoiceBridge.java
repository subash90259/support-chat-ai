package com.example.support_chat_ai;

import netscape.javascript.JSObject;

import java.io.File;

public class VoiceBridge {

    private final VoiceRecorder voiceRecorder = new VoiceRecorder();
    private final WhisperService whisperService = new WhisperService();

    private JSObject javascriptWindow;

    public void setJavascriptWindow(JSObject javascriptWindow) {
        this.javascriptWindow = javascriptWindow;
    }

    public void startRecording() {

        System.out.println("JS -> JAVA : START RECORDING");

        voiceRecorder.startRecording();
    }

    public void stopRecording() {

        System.out.println("JS -> JAVA : STOP RECORDING");

        File audioFile = voiceRecorder.stopRecording();

        System.out.println("Sending audio to Whisper...");

        String text = whisperService.transcribe(audioFile);

        System.out.println("WHISPER TEXT: " + text);

        if (javascriptWindow != null) {

            javascriptWindow.call(
                    "receiveVoiceText",
                    text
            );
        }
    }
}