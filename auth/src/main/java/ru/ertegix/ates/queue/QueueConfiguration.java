package ru.ertegix.ates.queue;

import ru.ertegix.ates.AtesAuthAppConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfiguration {

    @Bean
    public NewTopic usersStreamTopic() {
        return new NewTopic(AtesAuthAppConstants.USER_TOPIC_NAME, 1, (short) 1);
    }
}
