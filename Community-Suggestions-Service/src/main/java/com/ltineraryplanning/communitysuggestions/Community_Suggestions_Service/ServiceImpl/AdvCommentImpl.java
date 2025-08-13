package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.ServiceImpl;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.entity.Comment;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.entity.Suggestion;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.kafka.consumer.Options;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.kafka.consumer.PollDetailsDTO;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@Service
public class AdvCommentImpl {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void updateCommentText(String suggestionId, String commentId, String newText) {
        Query query = new Query(
                Criteria.where("_id").is(new ObjectId(suggestionId))
                        .and("comments.commentId").is(commentId)
        );

        Update update = new Update().set("comments.$.text", newText);

        mongoTemplate.updateFirst(query, update, Suggestion.class);
    }

    public void deleteCommentText(String suggestionId, String commentId, String newText) {
        Query query = new Query(
                Criteria.where("_id").is(new ObjectId(suggestionId))
                        .and("comments.commentId").is(commentId)
        );

        Update update = new Update().set("comments.$.text", newText);

        mongoTemplate.updateFirst(query, update, Suggestion.class);
    }

    public Comment getCommentById(String suggestionId, String commentId) {
        Suggestion suggestion = mongoTemplate.findOne(
                Query.query(Criteria.where("_id").is(new ObjectId(suggestionId))
                        .and("comments.commentId").is(commentId)),
                Suggestion.class
        );

        if (suggestion != null && suggestion.getComments() != null) {
            return suggestion.getComments().stream()
                    .filter(c -> c.getCommentId().equals(commentId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public void deleteComment(String suggestionId, String commentId) {
        Query query = new Query(
                Criteria.where("_id").is(new ObjectId(suggestionId))
        );

        Update update = new Update().pull("comments", Query.query(Criteria.where("commentId").is(commentId)).getQueryObject());

        mongoTemplate.updateFirst(query, update, Suggestion.class);
    }

    @Async
    public void addVote(String pollId, String optionId) {
        // Step 1: Get the poll by ID
        Query query = new Query(Criteria.where("_id").is(pollId));
        PollDetailsDTO poll = mongoTemplate.findOne(query, PollDetailsDTO.class);

        if (poll == null) {
            return;
        }

        // Step 2: Find the option and current votes
        List<Options> options = poll.getOptions();
        Options targetOption = options.stream()
                .filter(opt -> opt.getOptionId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Option not found"));

        int currentVotes = targetOption.getVotes();
        int newVotes = currentVotes + 1;

        // Step 3: Update the specific option's votes
        Update update = new Update()
                .set("options.$[opt].votes", newVotes)
                .filterArray(Criteria.where("opt.optionId").is(optionId));

        mongoTemplate.updateFirst(query, update, PollDetailsDTO.class);
    }

}
