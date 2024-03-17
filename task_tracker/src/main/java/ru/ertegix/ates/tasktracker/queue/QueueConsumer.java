package ru.ertegix.ates.tasktracker.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ertegix.ates.common.Role;
import ru.ertegix.ates.tasktracker.model.User;
import ru.ertegix.ates.tasktracker.repo.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import ru.ertegix.ates.event.UserEvent_v1;

import java.util.Optional;
import java.util.UUID;

import static ru.ertegix.ates.tasktracker.queue.TopicsNames.USER_STREAM_TOPIC_NAME;


@Slf4j
@Component
@RequiredArgsConstructor
public class QueueConsumer {

    private final UserRepository userRepository;

    @KafkaListener(topics = USER_STREAM_TOPIC_NAME)
    public void receive(@Payload UserEvent_v1 event)  {
        System.out.println("Got a user event: " + event);
        Optional<User> user = userRepository.findByPublicId(UUID.fromString(event.publicId));
        if (user.isPresent()) {
            User userFound = user.get();
            userFound.setRole(Role.valueOf(event.role));
            userRepository.saveAndFlush(userFound);
        } else {
            userRepository.saveAndFlush(
                    new User(
                            UUID.fromString(event.publicId),
                            event.username,
                            Role.valueOf(event.role))
            );
        }
    }
}
