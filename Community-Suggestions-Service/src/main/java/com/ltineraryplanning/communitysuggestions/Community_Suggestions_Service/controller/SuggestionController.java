package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.controller;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.*;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.enums.StatusCodeEnum;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.service.AiService;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.service.SuggestionService;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.service.VoteService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/api/v1/suggestion/")
public class SuggestionController {

    @Autowired
    SuggestionService suggestionService;

    @Autowired
    VoteService voteService;

    @Autowired
    AiService aiService;


    @RateLimiter(name = "accountRateLimiter", fallbackMethod = "addNewFallBack")
    @Bulkhead(name = "communityServiceRateLimiter", fallbackMethod = "addNewBulkFallback")
    @CircuitBreaker(name = "communityService", fallbackMethod = "addFallBack")
    @PostMapping("{tripID}/add")
    private ResponseDTO addNew(@RequestHeader("Authorization") String auth,@RequestBody AddSuggestionDTO addSuggestionDTO,@PathVariable String tripID) throws ParseException {
        return suggestionService.add(auth,addSuggestionDTO,tripID);
    }

    private ResponseDTO addNewFallBack(@RequestHeader("Authorization") String auth,@RequestBody AddSuggestionDTO addSuggestionDTO,@PathVariable String tripID, Throwable ex) {
        ResponseDTO response = new ResponseDTO("503", "Too many requests – please try again later.", null);
        return new ResponseDTO("503","Service not available",null);
    }

    private ResponseDTO addFallBack(@RequestHeader("Authorization") String auth,@RequestBody AddSuggestionDTO addSuggestionDTO,@PathVariable String tripID, Throwable ex) {
        ResponseDTO response = new ResponseDTO("503", "Too many requests – please try again later.", null);
        return new ResponseDTO("503","Circuit-Bracker Service not available",null);
    }

    private ResponseDTO addNewBulkFallback(@RequestHeader("Authorization") String auth,@RequestBody AddSuggestionDTO addSuggestionDTO,@PathVariable String tripID, Throwable ex)  {
        ResponseDTO response = new ResponseDTO("503", "Too many concurrent requests – please try again later.", null);
        return new ResponseDTO("503","Service not available",null);
}

        @DeleteMapping("{suggestionId}/delete")
    private ResponseDTO deleteSuggestion(@RequestHeader("Authorization") String auth,@PathVariable String suggestionId) throws ParseException {
        return suggestionService.deleteSuggestion(auth,suggestionId);
    }
    @GetMapping("{suggestionId}/view")
    private ResponseDTO viewSuggestion(@RequestHeader("Authorization") String auth,@PathVariable String suggestionId) throws ParseException {
        return suggestionService.viewSuggestion(auth,suggestionId);
    }

    @GetMapping("view-all")
    private ResponseDTO viewMyAll(@RequestHeader("Authorization") String auth) throws ParseException {
        return suggestionService.viewMyAllSuggestion(auth);
    }

    @PostMapping("{suggestionId}/comments")
    private ResponseDTO postComment(@RequestHeader("Authorization") String auth, @PathVariable String suggestionId, @Valid @RequestBody AddCommentDTO addCommentDTO, Errors errors) throws ParseException {
        if(errors.hasErrors()){
            return new ResponseDTO(StatusCodeEnum.ERROR.getStatusCode(), errors.getAllErrors().get(0).getDefaultMessage(),null);
        }
        return suggestionService.postComment(auth,suggestionId,addCommentDTO);
    }

    @PutMapping("{suggestionId}/comments/{commentId}")
    private ResponseDTO updateComment(@RequestHeader("Authorization") String auth,@PathVariable String suggestionId,@PathVariable String commentId,  @RequestBody AddCommentDTO addCommentDTO) throws ParseException {
        return suggestionService.updateComment(auth,suggestionId,commentId,addCommentDTO);
    }

    @DeleteMapping("{suggestionId}/comments/{commentId}")
    private ResponseDTO deleteComment(@RequestHeader("Authorization") String auth,@PathVariable String suggestionId,@PathVariable String commentId) throws ParseException {
        return suggestionService.deleteComment(auth,suggestionId,commentId);
    }

    @PostMapping("{suggestionId}/create-poll")
    private ResponseDTO createPoll(@RequestHeader("Authorization") String auth,@PathVariable String suggestionId,@Valid @RequestBody CommunitySuggestionPoll communitySuggestionPoll,Errors errors) throws ParseException {
        if(errors.hasErrors()){
            return new ResponseDTO(StatusCodeEnum.ERROR.getStatusCode(),errors.getAllErrors().get(0).getDefaultMessage(),null);
        }
        return suggestionService.createSuggestionPoll(auth,communitySuggestionPoll,suggestionId);
    }

    @PostMapping("{suggestionId}/like")
    private ResponseDTO upVote(@RequestHeader("Authorization") String auth,@PathVariable String suggestionId) throws ParseException {
        return voteService.likeSuggestion(auth,suggestionId);
    }

    @PostMapping("{suggestionId}/dislike")
    private ResponseDTO downVote(@RequestHeader("Authorization") String auth,@PathVariable String suggestionId) throws ParseException {
        return voteService.disLikeSuggestion(auth,suggestionId);
    }

    @PostMapping("{suggestionId}/remove-vote")
    public String removeVote(@RequestHeader("Authorization") String auth,@PathVariable String suggestionId) throws ParseException {
        voteService.removeVote(auth, suggestionId);
        return "Vote removed successfully";
    }

    @GetMapping("{suggestionId}/all-polls")
    public ResponseDTO getAllPolls(@RequestHeader("Authorization") String auth,@PathVariable String suggestionId) throws ParseException {
        return suggestionService.getAllPolls(auth, suggestionId);

    }

    @PostMapping("ask-ai/{question}")
    public ResponseDTO  getAiResponse(@RequestHeader("Authorization") String auth,@PathVariable String question) throws ParseException {
        return aiService.askAiResponse(auth, question);
    }


}
