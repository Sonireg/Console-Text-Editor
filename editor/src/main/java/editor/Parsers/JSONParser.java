package editor.Parsers;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class JSONParser extends Parser {

    @Override
    public List<StringBuilder> parse(List<StringBuilder> rawContent, int maxLength) {
        StringBuilder combined = new StringBuilder();
        for (StringBuilder line : rawContent) {
            combined.append(line.toString());
        }

        try {
            JsonElement jsonElement = JsonParser.parseString(combined.toString());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String prettyJson = gson.toJson(jsonElement);

            List<StringBuilder> formatted = new ArrayList<>();
            for (String line : prettyJson.split("\n")) {
                formatted.add(new StringBuilder(line));
            }
            return cutLines(formatted, maxLength);
        } catch (JsonSyntaxException e) {
            // Вернуть оригинал, если невалидный JSON
            List<StringBuilder> fallback = new ArrayList<>();
            fallback.add(new StringBuilder("Invalid JSON format"));
            return cutLines(fallback, maxLength);
        }
    }
}
