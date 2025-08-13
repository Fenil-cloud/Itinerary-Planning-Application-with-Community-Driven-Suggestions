package com.ltineraryplanning.votingandpoll.ServiceImpl;

import com.ltineraryplanning.votingandpoll.Entity.PollOptions;
import com.ltineraryplanning.votingandpoll.Entity.Polls;
import com.ltineraryplanning.votingandpoll.Kafka.CommunitySuggestionPoll;
import com.ltineraryplanning.votingandpoll.Kafka.producer.Options;
import com.ltineraryplanning.votingandpoll.Kafka.producer.PollDetailsDTO;
import com.ltineraryplanning.votingandpoll.Kafka.producer.kafkaProducer;
import com.ltineraryplanning.votingandpoll.Repo.PollOptionsRepo;
import com.ltineraryplanning.votingandpoll.Repo.PollVotesRepo;
import com.ltineraryplanning.votingandpoll.Repo.PollsRepo;
import com.ltineraryplanning.votingandpoll.service.SuggestionPollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaveService {

    @Autowired
    private PollsRepo pollsRepo;

    @Autowired
    private PollOptionsRepo pollOptionsRepo;

    @Autowired
    private PollVotesRepo pollVotesRepo;

    @Autowired
    private kafkaProducer kafkaProducer;




//    @Override
    @Async
    public void SaveNewPoll(CommunitySuggestionPoll suggestionPoll) {
        Polls polls = new Polls();
        polls.setCreatedAt(LocalDateTime.now());
        polls.setType(suggestionPoll.getType().toLowerCase());
        polls.setExpiresAt(LocalDateTime.now().plusHours(suggestionPoll.getExpireTime()));
        polls.setUserName(suggestionPoll.getUserName());
        polls.setTripId(suggestionPoll.getTripId());
        polls.setSuggestionId(suggestionPoll.getSuggestionID());
        polls.setQuestion((suggestionPoll.getQuestion()));

        List<PollOptions> optionsList = new ArrayList<>();
        for (String optionText : suggestionPoll.getOptions()) {
            PollOptions pollOption = new PollOptions();
            pollOption.setPoll(polls);
            pollOption.setOptionText(optionText);
            pollOption.setVotes(0L);
            optionsList.add(pollOption);
        }
        polls.setOptions(optionsList);
        var savedPoll = pollsRepo.save(polls);

        PollDetailsDTO polls1 = getPollDetailsDTO(savedPoll);
        kafkaProducer.sendSuggestionPoll(polls1);


    }

    private static PollDetailsDTO getPollDetailsDTO(Polls savedPoll) {
        PollDetailsDTO polls1 = new PollDetailsDTO();
        polls1.setPollID(savedPoll.getPollId().toString());
        polls1.setQuestion(savedPoll.getQuestion());
        polls1.setSuggestionId(savedPoll.getSuggestionId());
        polls1.setTripId(savedPoll.getTripId());
        polls1.setExpireTime(savedPoll.getExpiresAt().toString());
        polls1.setCreatedAt(savedPoll.getCreatedAt().toString());
        List<Options> options = new ArrayList<>();
        for(PollOptions saved: savedPoll.getOptions()){
            Options o = new Options();
            o.setVotes(0);
            o.setText(saved.getOptionText());
            o.setOptionId(saved.getOptionId().toString());
            String url =  "http://localhost:8222/api/v1/polls" + "/" + savedPoll.getPollId().toString() + "/" + saved.getOptionId().toString() + "/vote";;
            o.setUrl(url);
            options.add(o);
        }
        polls1.setOptions(options);
        return polls1;
    }
}
