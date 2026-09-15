package pl.training.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.training.springai.model.PromptRequest;

import java.util.Base64;

@RestController
public class ImageController {

    private final ImageModel imageModel;
    private final ChatClient  chatClient;

    public ImageController(ImageModel imageModel, ChatClient chatClient) {
        this.imageModel = imageModel;
        this.chatClient = chatClient;
    }

    /**
     * Image generation goes through ImageModel, a separate abstraction from ChatModel: it takes an
     * ImagePrompt and returns images rather than text. ImageOptionsBuilder is the portable options
     * builder, so the same code works across providers - only the values are provider-specific.
     * <p>
     * For OpenAI:
     * <ul>
     *   <li>model: dall-e-3 (better) or dall-e-2 (cheaper);</li>
     *   <li>width/height: 1024x1024, 1024x1792 or 1792x1024 for DALL-E-3;</li>
     *   <li>quality: standard or hd;</li>
     *   <li>style: vivid or natural.</li>
     * </ul>
     * responseFormat decides what the result carries: "url" gives a link that expires, "b64_json"
     * embeds the bytes in the response - which is why getB64Json() is read here instead of getUrl().
     */

    @PostMapping("generate-image")
    public ResponseEntity<byte[]> generateImage(@RequestBody PromptRequest promptRequest) {
        var options = ImageOptionsBuilder.builder()
                .model("gpt-image-2.5-flare")
                .width(1024)
                .height(1024)
                //.responseFormat("url")
                .build();
        var prompt = new ImagePrompt(promptRequest.userPromptText(), options);
        var image = imageModel.call(prompt)
                .getResult()
                .getOutput()
                //.getUrl()
                .getB64Json();
        var bytes = Base64.getDecoder().decode(image);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/png")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"image.png\"")
                .body(bytes);
    }

    @Value("classpath:slon-small.jpg")
    private Resource image;

    @PostMapping("generate-description")
    public String generateDescription(@RequestBody PromptRequest promptRequest) {
        return chatClient
                .prompt()
                .user(spec -> spec
                        .text(promptRequest.userPromptText())
                        .media(MediaType.IMAGE_PNG, image)
                )
                .call()
                .content();
    }

}
