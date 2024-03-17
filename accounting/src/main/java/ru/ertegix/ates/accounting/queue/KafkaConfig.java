package ru.ertegix.ates.accounting.queue;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import static ru.ertegix.ates.accounting.queue.TopicNames.*;

@Configuration
public class KafkaConfig {

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

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> greetingKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.afterPropertiesSet();
        return factory;
    }
}
