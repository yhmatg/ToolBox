package com.android.toolbox.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

public class DaterylaiGson {
    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new TypeAdapter<Date>() {
            @Override
            public void write(JsonWriter out, Date value) throws IOException {
                out.value(value.getTime());
            }

            @Override
            public Date read(JsonReader in) throws IOException {
                return new Date(in.nextLong());
            }
        });
        Gson gson = builder.create();
        return gson;
    }
}