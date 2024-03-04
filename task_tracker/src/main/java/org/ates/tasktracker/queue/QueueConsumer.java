package org.ates.tasktracker.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ates.tasktracker.AtesTaskAppConstants;
import org.ates.tasktracker.event.incoming_events.UserCreated;
import org.ates.tasktracker.model.User;
import org.ates.tasktracker.repo.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class QueueConsumer {

    private final UserRepository userRepository;

    @KafkaListener(topics = AtesTaskAppConstants.USER_TOPIC_NAME)
    public void receive(@Payload UserCreated event)  {
        System.out.println("Got a message");
        userRepository.saveUser(
                new User(event.getId(), event.getUsername()));
    }
}
