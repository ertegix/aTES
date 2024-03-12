package ru.ertegix.ates.tasktracker.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ertegix.ates.UserEvent;
import ru.ertegix.ates.tasktracker.AtesTaskAppConstants;
import ru.ertegix.ates.tasktracker.model.User;
import ru.ertegix.ates.tasktracker.repo.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class QueueConsumer {

    private final UserRepository userRepository;

    @KafkaListener(topics = AtesTaskAppConstants.USER_TOPIC_NAME)
    public void receive(@Payload UserEvent event)  {
        System.out.println("Got a message: " + event);
        userRepository.saveUser(
                new User(event.getPublicId(), event.getUsername()));
    }
}
