package com.eaut.footballclubmanagement.network;

import android.content.Context;
import android.content.SharedPreferences;

public final class SessionManager {
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EXPIRES_AT = "token_expires_at";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, String username, long expiresInSeconds) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("A non-empty token is required");
        }
        long safeSeconds = Math.max(0L, expiresInSeconds);
        long expiresAt = safeSeconds == 0L
                ? 0L
                : System.currentTimeMillis() + Math.min(safeSeconds, Long.MAX_VALUE / 1000L) * 1000L;
        preferences.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_TOKEN, token.trim())
                .putString(KEY_USERNAME, username == null ? "" : username.trim())
                .putLong(KEY_EXPIRES_AT, expiresAt)
                .apply();
    }

    public String getToken() {
        if (isExpired()) {
            clearAuth();
            return "";
        }
        String token = preferences.getString(KEY_TOKEN, "");
        return token == null ? "" : token.trim();
    }

    public String getUsername() {
        String username = preferences.getString(KEY_USERNAME, "");
        return username == null ? "" : username;
    }

    public boolean hasValidSession() {
        return preferences.getBoolean(KEY_LOGGED_IN, false) && !getToken().isEmpty();
    }

    public void clearAuth() {
        preferences.edit()
                .remove(KEY_LOGGED_IN)
                .remove(KEY_TOKEN)
                .remove(KEY_USERNAME)
                .remove(KEY_EXPIRES_AT)
                .apply();
    }

    private boolean isExpired() {
        long expiresAt = preferences.getLong(KEY_EXPIRES_AT, 0L);
        return expiresAt > 0L && System.currentTimeMillis() >= expiresAt;
    }
}
