package com.ltineraryplanning.gatewayservice.Kafka;

import com.ltineraryplanning.gatewayservice.Redis.RedisService;
import com.ltineraryplanning.gatewayservice.Redis.RedisTokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KafkaConsumer {
    @Autowired
    private RedisService service;

    @KafkaListener(topics = "${kafkaTopic.topic}")
    public void consumeAuthEmailTopic(RedisTokenDto redisTokenDto) throws MessagingException {
        log.info("Consuming the message from Auth-Topic:: {} ",redisTokenDto);

        try {
            Map<String,String> map = new HashMap<>();
            map.put("refresh_token", redisTokenDto.getRefresh_token());
            map.put("access_token",redisTokenDto.getAccess_token());
            service.saveRefreshToken(redisTokenDto.getId(),map,21600).subscribe(s->{
                if (s) {
                    log.info("Token saved!");
                } else {
                    log.warn("Token save failed!");
                }
            });
//       service.getRefreshToken("refresh:"+redisTokenDto.getId()).subscribe(v->log.info("refresh token {}",v));
        }catch (MessagingException exception){
            log.error("Save Redis Token error...{}",exception.getMessage());
        }
    }
}
