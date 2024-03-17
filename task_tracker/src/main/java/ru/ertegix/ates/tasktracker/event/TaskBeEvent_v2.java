package ru.ertegix.ates.tasktracker.event;

import org.apache.avro.specific.SpecificRecord;
import ru.ertegix.ates.event.TaskBusinessEvent_v1;


public class TaskBeEvent_v2 implements BusinessEvent {

    private final ru.ertegix.ates.event.TaskBusinessEvent_v2 content;

    public TaskBeEvent_v2(ru.ertegix.ates.event.TaskBusinessEvent_v2 content) {
        this.content = content;
    }

    public SpecificRecord getContent() {
        return content;
    }
}
