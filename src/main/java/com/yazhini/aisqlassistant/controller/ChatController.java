package com.yazhini.aisqlassistant.controller;

import com.yazhini.aisqlassistant.ai.AIService;
import com.yazhini.aisqlassistant.dto.QuestionRequest;
import com.yazhini.aisqlassistant.dto.QuestionResponse;
import com.yazhini.aisqlassistant.service.DatabaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final AIService aiService;
    private final DatabaseService databaseService;

    public ChatController(AIService aiService,
                          DatabaseService databaseService) {
        this.aiService = aiService;
        this.databaseService = databaseService;
    }

    @PostMapping("/chat")
    public QuestionResponse chat(@RequestBody QuestionRequest request) {

        String question = request.getQuestion();

        // Step 1: Generate SQL
        String sql = aiService.generateSQL(question);

        // Step 2: Execute SQL
        List<Map<String, Object>> result =
                databaseService.executeQuery(sql);

        // Step 3: AI explains the result
        String answer =
                aiService.explainResult(question, result);

        return new QuestionResponse(answer);
    }
}