package org.ates.queue;

import org.apache.kafka.clients.admin.NewTopic;
import org.ates.AtesAuthAppConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfiguration {

    @Bean
    public NewTopic usersStreamTopic() {
        return new NewTopic(AtesAuthAppConstants.USER_TOPIC_NAME, 1, (short) 1);
    }
}
