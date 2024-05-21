package ru.yandex.javacourse.zolotyh.schedule.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(duration.toString());
        }
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            return null;
        }
        return Duration.parse(jsonReader.nextString());
    }
}
