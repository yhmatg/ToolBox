package com.android.toolbox.app;


import com.gg.reader.api.dal.GClient;

public class GlobalClient {

    private GlobalClient() {

    }

    private enum Singleton {
        INSTANCE;

        private final GClient client;

        Singleton() {
            client = new GClient();
        }

        private GClient getInstance() {
            return client;
        }
    }

    public static GClient getClient() {

        return Singleton.INSTANCE.getInstance();
    }
}
