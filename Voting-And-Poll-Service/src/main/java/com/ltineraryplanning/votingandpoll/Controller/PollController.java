package com.ltineraryplanning.votingandpoll.Controller;

import com.ltineraryplanning.votingandpoll.dto.ResponseDTO;
import com.ltineraryplanning.votingandpoll.service.SuggestionPollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;


@RestController
@RequestMapping("/api/v1/polls/")
public class PollController {

    @Autowired
    SuggestionPollService suggestionPollService;

    @PostMapping("{pollId}/{optionID}/vote")
    public ResponseDTO pollVote(@RequestHeader("Authorization") String auth, @PathVariable Long pollId, @PathVariable Long optionID) throws ParseException {
        return suggestionPollService.votePoll(pollId,optionID,auth);

    }
}
