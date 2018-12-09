package io.molr.gui.fx.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Jsons {

    private static final JsonParser PARSER = new JsonParser();
    private static final Gson GSON
            = new GsonBuilder().setPrettyPrinting().create();

    private Jsons() {
        /* only static methods */
    }

    public static final String prettyPring(String input) {
        JsonObject json = PARSER.parse(input).getAsJsonObject();
        return GSON.toJson(json);
    }
}
