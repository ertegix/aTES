package ru.ertegix.ates.tasktracker.event;

import org.apache.avro.specific.SpecificRecord;
import ru.ertegix.ates.event.TaskBusinessEvent_v1;


public class TaskBeEvent_v1 implements BusinessEvent {

    private final TaskBusinessEvent_v1 content;

    public TaskBeEvent_v1(TaskBusinessEvent_v1 content) {
        this.content = content;
    }

    public SpecificRecord getContent() {
        return content;
    }
}
