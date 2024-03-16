package ru.ertegix.ates.tasktracker.event;

import org.apache.avro.specific.SpecificRecord;

public interface BusinessEvent {

    SpecificRecord getContent();
}
