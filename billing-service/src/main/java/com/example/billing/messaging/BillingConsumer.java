package com.example.billing.messaging;

import com.example.billing.config.RabbitConfig;
import com.example.events.InvalidPayloadException;
import com.example.events.OrderCreatedEvent;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class BillingConsumer {

    private final BillingEventHandler handler;

    public BillingConsumer(BillingEventHandler handler) {
        this.handler = handler;
    }

    @RabbitListener(queues = RabbitConfig.BILLING_QUEUE)
    public void onMessage(
            OrderCreatedEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws Exception {
        try {
            handler.handle(event);

//            success ->ACK
            channel.basicAck(deliveryTag, false);

        } catch (RetryableException e) {

//            retry -> NACK + requeue
            channel.basicNack(deliveryTag, false, true);

        } catch (InvalidPayloadException e) {

//            invalid payload -> NACK + No response ->DLQ
            channel.basicNack(deliveryTag, false, false);
        }
    }

}
