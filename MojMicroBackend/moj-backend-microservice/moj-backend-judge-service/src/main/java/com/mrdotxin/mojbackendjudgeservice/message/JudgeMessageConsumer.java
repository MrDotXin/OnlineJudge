package com.mrdotxin.mojbackendjudgeservice.message;

import com.mrdotxin.moj.backend.model.entity.QuestionSubmit;
import com.mrdotxin.moj.backend.model.vo.QuestionSubmitVO;
import com.mrdotxin.mojbackendjudgeservice.controller.JudgeInnerController;
import com.mrdotxin.mojbackendjudgeservice.service.JudgeService;
import com.mrdotxin.mojbackendserviceclient.service.QuestionFeignClient;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class JudgeMessageConsumer {

    @Resource
    private JudgeService judgeService;

    @SneakyThrows
    @RabbitListener(queues = {"moj_judge_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("获得判题申请 questionsubmit id {}", message);

            // 判题服务执行并且保存数据

            judgeService.doJudge(Long.parseLong(message));

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("未知错误");
            e.printStackTrace();
        }
    }
}
