package fr.syrows.smartcommands.contents;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.syrows.smartcommands.SmartCommandsAPI;
import fr.syrows.smartcommands.contents.usages.Usage;
import fr.syrows.smartcommands.utils.Utils;
import org.bukkit.util.StringUtil;

import java.util.*;

public class CommandUsage {

    private static final int MIN_SIMILARITY_PERCENTAGE = 35;

    private JsonObject object;

    public CommandUsage(JsonObject object){
        this.object = object;
    }

    public List<Usage> findUsages(String[] args) {

        List<Usage> usages = new ArrayList<>();

        JsonObject current = this.object;

        for(int i = 0; i < args.length; i++) {

            if(isUsage(current)) {
                usages.add(getAsUsage(current));
                break;
            }
            String argument = args[i].toLowerCase();

            List<String> keySet = getKeys(current.entrySet());

            //Valid argument
            if(keySet.contains(argument)) {

                if(i + 1 != args.length) {
                    current = current.get(argument).getAsJsonObject();
                    continue;
                }
                usages.addAll(findAllUsages(current.get(argument).getAsJsonObject()));
                break;
            }
            int found = 0;
            //Invalid argument
            for(String key : keySet) {

                //Check if startWith
                if(StringUtil.startsWithIgnoreCase(key, argument) || StringUtil.startsWithIgnoreCase(argument, key)) {

                    usages.addAll(findAllUsages(current.get(key).getAsJsonObject()));

                    found++;
                    continue;
                }

                //Check with Levenshtein distance
                double percentageOfSimilarity = Utils.getPercentageOfSimilarity(key, argument);

                if(percentageOfSimilarity >= MIN_SIMILARITY_PERCENTAGE) {

                    usages.addAll(findAllUsages(current.get(key).getAsJsonObject()));

                    found++;
                }
            }
            //No argument found. Getting all the usages of the current object.
            if(found == 0) usages.addAll(findAllUsages(current.getAsJsonObject()));

            //Stop loop because one of the arguments is invalid
            break;
        }
        return usages;
    }

    public List<Usage> findAllUsages(JsonObject object) {

        List<Usage> allUsages = new ArrayList<>();

        if(isUsage(object)) {
            allUsages.add(getAsUsage(object));
            return allUsages;
        }

        for(String key : getKeys(object.entrySet())) {

            JsonObject current = object.get(key).getAsJsonObject();

            if(isUsage(current)) {
                allUsages.add(getAsUsage(current));
                continue;
            }
            allUsages.addAll(findAllUsages(current));
        }

        return allUsages;
    }

    public Map<String, List<Usage>> getTabCompletes(String[] args) {

        Map<String, List<Usage>> tabCompletes = new HashMap<>();

        JsonObject current = this.object;

        if (isUsage(current)) return tabCompletes;

        for (String arg : args) {

            String argument = arg.toLowerCase();

            if (isUsage(current)) break;

            List<String> keys = getKeys(current.entrySet());

            if (keys.contains(argument)) {
                current = current.get(argument).getAsJsonObject();
                continue;
            }

            for (String key : keys) {

                if (!StringUtil.startsWithIgnoreCase(key, argument)) continue;

                List<Usage> usages = findAllUsages(current.get(key).getAsJsonObject());

                tabCompletes.put(key, usages);
            }
        }
        return tabCompletes;
    }

    public Usage getAsUsage(JsonObject object) {
        return SmartCommandsAPI.gson.fromJson(object, Usage.class);
    }

    public boolean isUsage(JsonObject object) {
        return object.has("type") && object.get("type").getAsString().equals("Usage.class");
    }

    private List<String> getKeys(Set<Map.Entry<String, JsonElement>> entries) {
        List<String> keySet = new ArrayList<>();
        entries.forEach(entry -> keySet.add(entry.getKey()));
        return keySet;
    }
}
