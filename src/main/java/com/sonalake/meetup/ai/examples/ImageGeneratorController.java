package com.sonalake.meetup.ai.examples;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG;

@RestController
@RequestMapping("/ai")
public class ImageGeneratorController {
    private final OpenAiImageModel openaiImageModel;
    private final RestTemplate restTemplate;

    private OpenAiImageOptions openAiImageOptions;

    public ImageGeneratorController(OpenAiImageModel openaiImageModel, RestTemplate restTemplate) {
        this.openaiImageModel = openaiImageModel;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        openAiImageOptions = OpenAiImageOptions.builder()
                .model("dall-e-3")
                .quality("hd")
                .N(1)
                .height(1024)
                .width(1024)
                .build();
    }

    /**
     * Generates an image based on the provided prompt and returns it as a ResponseEntity
     * containing the image data in byte[] format.
     *
     * @param prompt the text prompt describing the image to generate; defaults to "AI future is exciting"
     *               if not provided
     * @return a ResponseEntity containing the generated image data as a byte array with appropriate
     *         headers, or an error message if the operation fails
     */
    @GetMapping("image")
    public ResponseEntity<byte[]> generateImage(@RequestParam(defaultValue = "AI future is exciting", name = "prompt") String prompt) {

        ImagePrompt imagePrompt = new ImagePrompt(prompt, openAiImageOptions);
        String imageUrl = openaiImageModel.call(imagePrompt).getResult().getOutput().getUrl();

        return downloadImage(imageUrl);
    }

    /**
     * Downloads an image from the specified URL and returns it as a ResponseEntity containing the image data in byte[] format.
     * If an error occurs while processing the image, a ResponseEntity with an error message is returned.
     *
     * @param imageUrl the URL of the image to download
     * @return a ResponseEntity containing the image data as a byte array with appropriate headers, or an error message if the operation fails
     */
    private ResponseEntity<byte[]> downloadImage(String imageUrl) {
        try {
            byte[] imageBytes = restTemplate.getForObject(new URI(imageUrl), byte[].class);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(IMAGE_JPEG);
            headers.setCacheControl("no-cache");

            return new ResponseEntity<>(imageBytes, headers, OK);

        } catch (URISyntaxException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error occurred while processing the image: " + e.getMessage()).getBytes());
        }
    }
}
