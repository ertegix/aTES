package ru.ertegix.ates.tasktracker.queue;

import org.apache.kafka.clients.admin.NewTopic;
import ru.ertegix.ates.tasktracker.AtesTaskAppConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Первичное создание необходимых топиков
 */

@Configuration
public class QueueConfig {

    @Bean
    public NewTopic tasksTopic() {
        return new NewTopic("tasks", 1, (short) 1);
    }

    @Bean
    public NewTopic usersTopic() {
        return new NewTopic(AtesTaskAppConstants.USER_TOPIC_NAME, 1, (short) 1);
    }
}
