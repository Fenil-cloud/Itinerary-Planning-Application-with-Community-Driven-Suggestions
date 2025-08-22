package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.ServiceImpl;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.ResponseDTO;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.entity.Suggestion;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.enums.StatusCodeEnum;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.repo.SuggestionRepo;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.service.AiService;


import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpringAIServiceImpl implements AiService {


    @Autowired
    private QdrantVectorStore vectorStore;

    @Autowired
    private SuggestionRepo suggestionRepo;

    @Autowired
    private OpenAiEmbeddingModel embeddingClient;

    @Autowired
    private OpenAiChatModel openAiChatModel;


    public void addToVectorDb() {
        List<Suggestion> suggestions = suggestionRepo.findByIsEmbeddedFalse();
        List<Document> documents = new ArrayList<>();

        for (Suggestion sug : suggestions) {
            StringBuilder sb = new StringBuilder();
            sb.append("Title: ").append(sug.getTitle()).append(". ");
            sb.append("Description: ").append(sug.getDescription()).append(". ");

            if (sug.getComments() != null && !sug.getComments().isEmpty()) {
                sb.append("User Comments:\n");
                sug.getComments().stream().limit(3).forEach(c -> {
                    sb.append("- ").append(c.getText()).append("\n");
                });
            }
            String textToEmbed = sb.toString();
            // Create a Document that includes both content and metadata (optional)
            Document doc = new Document(textToEmbed);
            doc.getMetadata().put("id", String.valueOf(sug.getSuggestionId()));
            doc.getMetadata().put("title", sug.getTitle());
            documents.add(doc);
            sug.setIsEmbedded(true);
            suggestionRepo.save(sug);
        }

        // Add all documents to Qdrant vector store
//        vectorStore.add(documents);

        int batchSize = 100;
        for (int i = 0; i < documents.size(); i += batchSize) {
            int end = Math.min(i + batchSize, documents.size());
            List<Document> batch = documents.subList(i, end);
            vectorStore.add(batch);
        }

    }
    public void result(){
//        List<Document> results = vectorStore.similaritySearch("manali");
//        List<Document> results = vectorStore.similaritySearch("manali");
//        results.stream()
//                .filter(doc -> (Float) doc.getMetadata().get("distance") < 0.7F)
//                .forEach(doc -> {
//                    System.out.println("Result: " + doc.getText());
//                    System.out.println("Metadata: " + doc.getMetadata());
//                });
        // Step 1: Retrieve similar docs from vector DB
//        String userQuestion = "must visit place in Manali.";

    }


    @Override
    public ResponseDTO askAiResponse(String auth, String userQuestion) {
        try {
            List<Document> retrievedDocs = vectorStore.similaritySearch(userQuestion);

            String context = retrievedDocs.stream()
                    .map(Document::getText)
                    .limit(3)
                    .collect(Collectors.joining("\n---\n"));
            System.out.println(context);
            if (context.trim().isEmpty()) {
                return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), userQuestion, "I'm sorry, I don't have information");
            }

            String fullPrompt = String.format("""
                    You are a travel assistant. Use the context below to answer the user's question.
                    
                     Context:
                     %s
                    
                     Question:
                     %s
                    
                     Answer:
                    If the answer is not in the context, politely say you don't know.
                    """, context, userQuestion);
            ChatResponse response = openAiChatModel.call(new Prompt(fullPrompt));
//        System.out.println("Answer from GPT:");
//        System.out.println(response.getResult().getOutput().getText());
            return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), userQuestion, response.getResult().getOutput().getText());
        }catch (Exception e){
            return new ResponseDTO(StatusCodeEnum.ERROR.getStatusCode(), userQuestion, "Something went wrong please try again later");

        }
    }
}

