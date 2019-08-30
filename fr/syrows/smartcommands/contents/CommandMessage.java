package fr.syrows.smartcommands.contents;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import fr.syrows.smartcommands.SmartCommandsAPI;

import java.lang.reflect.Type;
import java.util.List;

public class CommandMessage {

    private JsonObject object;

    public CommandMessage(JsonObject object) {
        this.object = object;
    }

    public String getString(String path) {
        return get(path).getAsString();
    }

    public int getInt(String path) {
        return get(path).getAsInt();
    }

    public double getDouble(String path) {
        return get(path).getAsDouble();
    }

    public long getLong(String path) {
        return get(path).getAsLong();
    }

    public <T> List<T> getList(String path) {
        Type type = new TypeToken<List<T>>(){}.getType();
        return SmartCommandsAPI.gson.fromJson(get(path), type);
    }

    public JsonElement get(String path) {

        if(path == null)
            throw new IllegalArgumentException("Path cannot be null.");

        if(this.object == null)
            throw new IllegalArgumentException("Cannot find a valid JsonObject.");

        JsonObject current = this.object;

        String[] index = path.split("\\.");

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < index.length; i++) {

            String key = index[i];

            sb.append("key");

            if(!current.has(key))
                throw new NullPointerException(String.format("The element %s was not found at the path %s", key, path));

            JsonElement element = current.get(key);

            if(!element.isJsonObject()) {

                if(i + 1 == index.length) return element;
                else throw new NullPointerException(String.format("Cannot find a JsonObject at %s. Found: %s", sb.toString(), element.getClass().getName()));
            }
            current = element.getAsJsonObject();
            sb.append(".");
        }
        return null;
    }
}
