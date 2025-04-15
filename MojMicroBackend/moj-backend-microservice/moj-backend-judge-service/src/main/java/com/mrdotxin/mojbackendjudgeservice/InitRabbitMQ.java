package com.mrdotxin.mojbackendjudgeservice;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitRabbitMQ {
    public static void doInit(){
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String exchangeName = "moj_judge_exchange";
            channel.exchangeDeclare(exchangeName, "direct");

            String queueName = "moj_judge_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, exchangeName, "moj_judge_routing_key");

            log.info("创建成功!");
        } catch (Exception e) {
            log.error("创建失败!");
        }
    }
}
