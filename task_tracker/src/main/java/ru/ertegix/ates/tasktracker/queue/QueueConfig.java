package ru.ertegix.ates.tasktracker.queue;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static ru.ertegix.ates.tasktracker.queue.TopicsNames.*;

/**
 * Первичное создание необходимых топиков
 */

@Configuration
public class QueueConfig {

    @Bean
    public NewTopic tasksTopic() {
        return new NewTopic(TASK_TOPIC_NAME, 1, (short) 1);
    }

    @Bean
    public NewTopic tasksStreamTopic() {
        return new NewTopic(TASK_STREAM_TOPIC_NAME, 1, (short) 1);
    }


    @Bean
    public NewTopic usersTopic() {
        return new NewTopic(USER_STREAM_TOPIC_NAME, 1, (short) 1);
    }
}
