package com.ltineraryplanning.votingandpoll.ServiceImpl;

import com.ltineraryplanning.votingandpoll.Entity.PollOptions;
import com.ltineraryplanning.votingandpoll.Entity.PollVotes;
import com.ltineraryplanning.votingandpoll.Entity.Polls;
import com.ltineraryplanning.votingandpoll.Kafka.producer.VoteDataDTO;
import com.ltineraryplanning.votingandpoll.Kafka.producer.kafkaProducer;
import com.ltineraryplanning.votingandpoll.Repo.PollOptionsRepo;
import com.ltineraryplanning.votingandpoll.Repo.PollVotesRepo;
import com.ltineraryplanning.votingandpoll.Repo.PollsRepo;
import com.ltineraryplanning.votingandpoll.dto.ResponseDTO;
import com.ltineraryplanning.votingandpoll.enums.StatusCodeEnum;
import com.ltineraryplanning.votingandpoll.service.SuggestionPollService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class SuggestionServiceImpl implements SuggestionPollService {
    @Autowired
    private PollsRepo pollsRepo;

    @Autowired
    private PollOptionsRepo pollOptionsRepo;

    @Autowired
    private PollVotesRepo pollVotesRepo;

    @Autowired
    private ExtractTokenService tokenService;


    @Autowired
    private kafkaProducer producer;

    @Override
    public ResponseDTO votePoll(Long pollId, Long optionID,String auth) throws ParseException {
        Map<String, Object> user = tokenService.extractValue(auth);

        Optional<Polls> polls = pollsRepo.findById(pollId);
        var pollOption = pollOptionsRepo.findById(optionID);

        if(pollOption.isEmpty() || polls.isEmpty()){
            return new ResponseDTO(StatusCodeEnum.ERROR.getStatusCode(),"poll or option not found",null);
        }
        if (polls.isPresent() && polls.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            return new ResponseDTO(
                    StatusCodeEnum.ERROR.getStatusCode(),
                    "Voting has ended for this poll.",
                    null
            );
        }        var voterOption = pollVotesRepo.findByUserIdAndPollId(user.get("uid").toString(),pollId);
        if(voterOption.isEmpty()) {


            PollVotes pollVotes = new PollVotes();
            pollVotes.setCreatedAt(LocalDateTime.now());
            pollVotes.setUserName(user.get("preferred_username").toString());
            pollVotes.setUserId(user.get("uid").toString());
            pollVotes.setOption(pollOption.get());
            pollVotes.setPollId(pollId);
            pollOption.get().setVotes(pollOption.get().getVotes() + 1);
            var response = pollVotesRepo.save(pollVotes);
//            var data = pollOptionsRepo.findTopByPoll_PollIdOrderByVotesDesc(pollId);
          List<PollOptions> data= pollOptionsRepo.findByPoll(pollsRepo.findById(pollId).get());
            Optional<PollOptions> maxVotedOption = data.stream()
                    .max(Comparator.comparingLong(PollOptions::getVotes));

            maxVotedOption.ifPresent(option -> {
//                System.out.println("Option with max votes: " + option.getOptionText() + " with " + option.getVotes() + " votes.");
                VoteDataDTO voteDataDTO = new VoteDataDTO();
                voteDataDTO.setPollId(pollId.toString());
                voteDataDTO.setMaxVoteId(String.valueOf(option.getOptionId()));
                voteDataDTO.setMaxVotes(option.getVotes().intValue());
                voteDataDTO.setOptionId(optionID.toString());
                producer.sendSuggestionPollVote(voteDataDTO);
            });







            return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), "Vote submitted successfully", null);
        }
        return new ResponseDTO(StatusCodeEnum.NOT_FOUND.getStatusCode(), "One vote per user is allowed",null);
    }
}
