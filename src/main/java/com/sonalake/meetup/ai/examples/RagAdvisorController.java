package com.sonalake.meetup.ai.examples;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING;
import static com.sonalake.meetup.utils.Utils.resourceToString;


@RestController
@RequestMapping("/ai")
public class RagAdvisorController {
    private final ChatClient chatClient;
    private final SimpleVectorStore simpleVectorStore;

    @Value( "classpath:/prompts/rag-prompt.st")
    private Resource ragPromptResource;

    @Value( "classpath:/prompts/rag-prompt-advisor.st")
    private Resource ragPromptAdvisorResource;

    public RagAdvisorController(ChatClient chatClient, SimpleVectorStore simpleVectorStore) {
        this.chatClient = chatClient;
        this.simpleVectorStore = simpleVectorStore;
    }

    @GetMapping("rag")
    public String rag(@RequestParam(defaultValue = "What are the two postulates of AiMeetup relativity ?", name = "question") String question) {
        SearchRequest searchRequest = SearchRequest.builder().query(question).topK(10).build();
        List<Document> documents = simpleVectorStore.doSimilaritySearch(searchRequest);

        Map<String, Object> model = getModel(question, documents);
        String prompt = new PromptTemplate(ragPromptResource).create(model).getContents();

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
    }

    @GetMapping("rag/advisor")
    public String ragAdvisor(@RequestParam(defaultValue = "What are the two postulates of AiMeetup relativity ?", name = "question") String question) {
        SearchRequest searchRequest = SearchRequest.builder().query(question).topK(10).build();
        String userTextAdvise = resourceToString(ragPromptAdvisorResource);

        return chatClient
                .prompt()
                .user(question)
                .advisors(
                        new SafeGuardAdvisor(List.of("fool", "stupid")),
                        new QuestionAnswerAdvisor(simpleVectorStore, searchRequest, userTextAdvise)
                )
                .call()
                .content();
    }

    /**
     * Constructs a model containing a question and the text of the provided documents.
     *
     * @param question  the question string to be included in the model
     * @param documents a list of documents whose text content will be aggregated
     * @return a map containing the question as "question" and the aggregated document text as "documents"
     */
    private Map<String, Object> getModel(String question, List<Document> documents) {
        Optional<String> docs = documents.stream().map(Document::getText).reduce((a, b) -> a + "\n" + b);

        return Map.of("question", question,
                      "documents", docs.orElse(EMPTY_STRING)
        );
     }
}
