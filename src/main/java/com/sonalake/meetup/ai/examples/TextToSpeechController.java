package com.sonalake.meetup.ai.examples;

import jakarta.annotation.PostConstruct;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3;
import static org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest.Voice.NOVA;
import static org.springframework.ai.openai.api.OpenAiAudioApi.TtsModel.TTS_1;

@RestController
@RequestMapping("/ai")
public class TextToSpeechController {
    @Value("${spring.ai.openai.api-key}")
    private String openAiToken;

    private OpenAiAudioSpeechModel speechModel;
    private OpenAiAudioSpeechOptions speechOptions;

    private final ExecutorService executorService = newSingleThreadExecutor();

    @PostConstruct
    public void init() {
        speechModel = new OpenAiAudioSpeechModel(new OpenAiAudioApi(openAiToken));
        speechOptions = OpenAiAudioSpeechOptions.builder()
                .responseFormat(MP3)
                .voice(NOVA)
                .speed(1.0f)
                .model(TTS_1.value)
                .build();
    }

    /**
     * Handles text-to-speech conversion for the provided input text.
     *
     * @param prompt the input text to be converted into speech, with a default value of
     *               "Hello, this is a text-to-speech example" if not specified
     * @return the original input text submitted for conversion
     */
    @RequestMapping("tts")
    public String tts(@RequestParam(defaultValue = "Hello, this is a text-to-speech example", name = "prompt") String prompt) {

        return textToSpeech(prompt);
    }

    /**
     * Converts the given text into speech and plays the audio output asynchronously.
     *
     * @param text the input text to be converted into speech
     * @return the original input text
     */
    public String textToSpeech(String text){
        SpeechPrompt speechPrompt = new SpeechPrompt(text, speechOptions);
        byte[] speechOutput = speechModel.call(speechPrompt).getResult().getOutput();

        executorService.submit(() -> {
            try {
                InputStream inputStream = new ByteArrayInputStream(speechOutput);

                Player player = new Player(inputStream);
                player.play();
            } catch (JavaLayerException e) {
                throw new RuntimeException(e);
            }
        });

        return text;
    }
}
