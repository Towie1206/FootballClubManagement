package com.eaut.footballclubmanagement.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import retrofit2.Response;

public final class NetworkErrorParser {
    private NetworkErrorParser() {
    }

    public static String message(Response<?> response, String fallback) {
        if (response == null || response.errorBody() == null) {
            return fallback;
        }
        try {
            return messageFromJson(response.errorBody().string(), fallback);
        } catch (IOException ignored) {
            return fallback;
        }
    }

    static String messageFromJson(String json, String fallback) {
        try {
            JsonElement rootElement = new JsonParser().parse(json);
            if (!rootElement.isJsonObject()) {
                return fallback;
            }
            JsonObject root = rootElement.getAsJsonObject();
            JsonObject error = root.has("error") && root.get("error").isJsonObject()
                    ? root.getAsJsonObject("error") : null;
            if (error != null && error.has("message") && !error.get("message").isJsonNull()) {
                String message = error.get("message").getAsString().trim();
                return message.isEmpty() ? fallback : message;
            }
            if (root.has("message") && !root.get("message").isJsonNull()) {
                String message = root.get("message").getAsString().trim();
                return message.isEmpty() ? fallback : message;
            }
        } catch (RuntimeException ignored) {
            // Malformed or unexpected error bodies must never crash the UI.
        }
        return fallback;
    }
}
