package com.mrdotxin.mojbackendquestionservice.message;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class JudgeRequestMessageProducer {
    public static final String MOJ_JUDGE_EXCHANGE = "moj_judge_exchange";
    public static final String MOJ_JUDGE_ROUTING_KEY = "moj_judge_routing_key";

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendJudgeRequestMessage(String message){
        rabbitTemplate.convertAndSend(MOJ_JUDGE_EXCHANGE, MOJ_JUDGE_ROUTING_KEY, message);
    }
}
