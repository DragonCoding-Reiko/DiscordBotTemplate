package net.dragoncoding.discordbottemplate.discordbot.utils;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TokenHandler {
    private static final String BOT_TOKEN_FILENAME = "Token.json";
    private static final String BOT_TOKEN_JSON_PROPERTY = "token";

    private static final Logger LOG = LoggerFactory.getLogger(TokenHandler.class);

    public static String getDiscordToken() {
        String jsonContent;

        try {
            jsonContent = new String(Files.readAllBytes(Paths.get(BOT_TOKEN_FILENAME)));
        } catch (IOException e) {
            LOG.error("Error reading the token file '" + BOT_TOKEN_FILENAME + "'", e);
            throw new RuntimeException(e);
        }

        JSONObject obj = new JSONObject(jsonContent);
        return obj.getString(BOT_TOKEN_JSON_PROPERTY);
    }
}
