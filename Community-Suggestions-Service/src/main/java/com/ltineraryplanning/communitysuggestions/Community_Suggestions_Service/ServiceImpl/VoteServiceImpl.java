package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.ServiceImpl;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.ResponseDTO;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.entity.Suggestion;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.enums.StatusCodeEnum;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.service.VoteService;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ExtractTokenService tokenService;
    



    @Override
    public ResponseDTO likeSuggestion(String auth, String suggestionId) throws ParseException {
        Map<String, Object> user = tokenService.extractValue(auth);
        String userId = user.get("preferred_username").toString();

        // Ensure correct _id type
        Object idValue = ObjectId.isValid(suggestionId)
                ? new ObjectId(suggestionId)
                : suggestionId;

        // Match only if user hasn't already voted
        Query query = new Query(Criteria.where("_id").is(idValue)
                .and("upVoters").nin(userId)
                .and("downVoters").nin(userId));

        Update update = new Update()
                .inc("upVotes", 1)
                .addToSet("upVoters", userId);

        UpdateResult result = mongoTemplate.updateFirst(query, update, Suggestion.class);

        if (result.getModifiedCount() == 0) {
            return new ResponseDTO(StatusCodeEnum.BAD_REQUEST.getStatusCode(),
                    "You already voted on this suggestion", null);
        }
        return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), "Suggestion liked", null);
    }

    @Override
    public ResponseDTO disLikeSuggestion(String auth, String suggestionId) throws ParseException {
        Map<String, Object> user = tokenService.extractValue(auth);
        String userId = user.get("preferred_username").toString();

        Object idValue = ObjectId.isValid(suggestionId)
                ? new ObjectId(suggestionId)
                : suggestionId;

        Query query = new Query(Criteria.where("_id").is(idValue)
                .and("upVoters").nin(userId)
                .and("downVoters").nin(userId));

        Update update = new Update()
                .inc("downVotes", 1)
                .addToSet("downVoters", userId);

        UpdateResult result = mongoTemplate.updateFirst(query, update, Suggestion.class);

        if (result.getModifiedCount() == 0) {
            return new ResponseDTO(StatusCodeEnum.BAD_REQUEST.getStatusCode(),
                    "You already voted on this suggestion", null);
        }
        return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), "Suggestion disliked", null);
    }

    @Override
    public void removeVote(String auth, String suggestionId) throws ParseException {
        Map<String, Object> user = tokenService.extractValue(auth);
        String userId = user.get("preferred_username").toString();

        Object idValue = ObjectId.isValid(suggestionId)
                ? new ObjectId(suggestionId)
                : suggestionId;

        // Remove upvote if exists
        Query upvoteQuery = new Query(Criteria.where("_id").is(idValue)
                .and("upVoters").in(userId));
        Update removeUpvote = new Update()
                .inc("upVotes", -1)
                .pull("upVoters", userId);
        mongoTemplate.updateFirst(upvoteQuery, removeUpvote, Suggestion.class);

        // Remove downvote if exists
        Query downvoteQuery = new Query(Criteria.where("_id").is(idValue)
                .and("downVoters").in(userId));
        Update removeDownvote = new Update()
                .inc("downVotes", -1)
                .pull("downVoters", userId);
        mongoTemplate.updateFirst(downvoteQuery, removeDownvote, Suggestion.class);
    }

}
