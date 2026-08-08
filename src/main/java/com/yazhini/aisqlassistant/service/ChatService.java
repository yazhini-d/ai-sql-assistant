package com.yazhini.aisqlassistant.service;

import com.yazhini.aisqlassistant.ai.AIService;
import com.yazhini.aisqlassistant.dto.QuestionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    @Autowired
    private AIService aiService;

    public QuestionResponse processQuestion(String question) {

        String sql = aiService.generateSQL(question);

        return new QuestionResponse(sql);

    }

}