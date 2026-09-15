package pl.training.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.training.springai.model.PromptRequest;
import pl.training.springai.tools.DateTimeTool;
import pl.training.springai.tools.DoubleValue;
import pl.training.springai.tools.PowerTool;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
public class ToolsController {

    private final ChatClient chatClient;

    public ToolsController(@Qualifier("ollamaChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("chat-with-tools")
    public Flux<String> chatWithTools(@RequestBody PromptRequest  promptRequest) {
       // var callbacks = ToolCallbacks.from(new DateTimeTool());
        var callbacks = FunctionToolCallback.builder("power", new PowerTool())
            // .description("Calculates the square of a number (value * value)")
                .inputType(DoubleValue.class)
                .build();

        return chatClient.prompt()
                .tools(new DateTimeTool(), callbacks)
                .toolContext(Map.of("userId", "12345"))
                .user(promptRequest.userPromptText())
                .stream()
                .content();
    }

}
