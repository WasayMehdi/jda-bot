package com.discord.texttospeech;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import lombok.Getter;

import java.io.*;

class TextToSpeechMp3 {

    private static final double SPEAKING_RATE = 0.7;

    @Getter
    private final File audioFile;

    TextToSpeechMp3(final String text) throws IOException {
        audioFile = createMp3(text);
    }

    /** Demonstrates using the Text-to-Speech API. */
    private File createMp3(final String text) throws IOException {
        // Instantiates a client
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            // Build the voice request, select the language code ("en-US") and the ssml voice gender
            // ("neutral")
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US")
                    .setSsmlGender(SsmlVoiceGender.MALE)
                    .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.OGG_OPUS)
                    .setSpeakingRate(SPEAKING_RATE).build();

            // Perform the text-to-speech request on the text input with the selected voice parameters and
            // audio file type
            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            final File temp = File.createTempFile("audio_tmp", "");
            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream(temp)) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file: " + temp.getAbsolutePath());
            }
            return temp;
        }
    }

    public void destroy() {
        if (audioFile.exists()) {
            if (audioFile.delete()) {
                System.out.println("Successfully deleted file: " + audioFile.getName());
            } else {
                System.out.println("Failed to delete temp file: " + audioFile.getName());
            }
        }
    }
}
