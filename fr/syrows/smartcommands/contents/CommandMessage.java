package fr.syrows.smartcommands.contents;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import fr.syrows.smartcommands.SmartCommandsAPI;
import fr.syrows.smartcommands.tools.EasyComponent;
import net.md_5.bungee.api.chat.TextComponent;

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

    public byte getByte(String path) {
        return get(path).getAsByte();
    }

    public short getShort(String path) {
        return get(path).getAsShort();
    }

    public int getInt(String path) {
        return get(path).getAsInt();
    }

    public double getDouble(String path) {
        return get(path).getAsDouble();
    }

    public float getFloat(String path) {
        return get(path).getAsFloat();
    }

    public long getLong(String path) {
        return get(path).getAsLong();
    }

    public EasyComponent getEasyComponent(String path) {
        return EasyComponent.deserialize(get(path).getAsJsonObject());
    }

    public TextComponent getTextComponent(String path) {
        return EasyComponent.deserialize(get(path).getAsJsonObject()).getAsTextComponent();
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

        for(int i = 0; i < index.length; i++) {

            String key = index[i];

            if(!current.has(key))
                throw new NullPointerException(String.format("Cannot find a JsonElement at '%s'.", path));

            JsonElement element = current.get(key);

            if(i + 1 == index.length) return element;
            else if(!element.isJsonObject()) throw new NullPointerException(String.format("Element at '%s' not found.", path));

            current = element.getAsJsonObject();
        }
        return null;
    }
}
