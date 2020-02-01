package fr.syrows.smartcommands.contents;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.syrows.smartcommands.SmartCommand;
import fr.syrows.smartcommands.contents.usages.Usage;
import fr.syrows.smartcommands.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommandUsage {

    private static final int MIN_SIMILARITY_PERCENTAGE = 35;

    private JsonObject container;

    public CommandUsage(JsonObject object) {
        this.container = object;
    }

    public List<Usage> findUsages(CommandSender sender, String[] args) {

        if(Usage.isUsage(this.container))
            return Collections.singletonList(Usage.deserialize(this.container));

        List<Usage> usages = new ArrayList<>();

        JsonObject current = this.container;

        for(int i = 0; i < args.length; i++) {

            String argument = args[i].toLowerCase();

            //If the argument is not contained, all keys are verified.
            if(!contains(current, argument)) {

                /*
                For each key, we retrieve all possible usages.
                If the key matches with the current argument, all the usages are added to the 'usages' list.
                 */

                for(Map.Entry<String, JsonElement> entry : current.entrySet()) {

                    String key = entry.getKey();

                    if(!current.get(key).isJsonObject()) break;

                    JsonObject object = current.get(key).getAsJsonObject();

                    List<Usage> list = findAllUsages(sender, object);

                    if(match(key, argument)) usages.addAll(list);
                }

            } else {

                /*
                If all the elements have been analyzed, we search all the usages from the current element (one and more).
                Otherwise, we continue the research by redefining the current object.
                 */

                if(i + 1 == args.length) {

                    usages.addAll(findAllUsages(sender, current.get(argument).getAsJsonObject()));
                    i = args.length;

                } else current = current.get(argument).getAsJsonObject();
            }
        }
        //If no usage was found, all the usages are returned.
        return usages.size() != 0 ? usages : findAllUsages(sender, current);
    }

    private List<Usage> findAllUsages(CommandSender sender, JsonObject object) {

        List<Usage> usages = new ArrayList<>();

        if(Usage.isUsage(object)) {

            Usage usage = Usage.deserialize(object);

            if(canExecuteUsage(sender, usage)) usages.add(usage);

            return usages;
        }

        for(Map.Entry<String, JsonElement> entry : object.entrySet()) {

            if(!object.get(entry.getKey()).isJsonObject()) break;

            JsonObject current = object.get(entry.getKey()).getAsJsonObject();

            if(Usage.isUsage(current)) {

                Usage usage = Usage.deserialize(current);

                if(canExecuteUsage(sender, usage)) usages.add(usage);

            } else usages.addAll(findAllUsages(sender, current));
        }
        return usages;
    }

    private boolean match(String reference, String argument) {

        if(StringUtil.startsWithIgnoreCase(reference, argument) || StringUtil.startsWithIgnoreCase(argument, reference))
            return true;

        double similarityPercentage = Utils.getPercentageOfSimilarity(reference, argument);

        return similarityPercentage >= MIN_SIMILARITY_PERCENTAGE;
    }

    private boolean canExecuteUsage(CommandSender sender, Usage usage) {
        return !usage.hasPermission() || sender.hasPermission(usage.getPermission());
    }

    private boolean contains(JsonObject container, String key) {
        return container.entrySet().stream().anyMatch(entry -> entry.getKey().equals(key));
    }

    public List<String> findCompletions(CommandSender sender, SmartCommand command, String[] args) {

        List<String> specialCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        JsonObject current = this.container;

        for(int i = 0; i < args.length; i++) {

            if(Usage.isUsage(current)) return completions;

            String argument = args[i].toLowerCase();

            if(!contains(current, argument)) {

                for(Map.Entry<String, JsonElement> entry : current.entrySet()) {

                    String key = entry.getKey();

                    if(isCompletion(key)) {

                        if(!isSpecialCompletion(key)) {

                            if(!StringUtil.startsWithIgnoreCase(key, argument)) continue;

                            List<Usage> usages = findAllUsages(sender, current);

                            boolean canExecute = usages.stream().anyMatch(usage -> canExecuteUsage(sender, usage));

                            if(canExecute) completions.add(key);

                        } else {

                            if(i + 1 == args.length) specialCompletions.addAll(getSpecialCompletions(command, key));
                            else current = current.get(key).getAsJsonObject();
                        }

                    } else if(i + 1 != args.length) current = current.get(key).getAsJsonObject();
                }

            } else current = current.get(argument).getAsJsonObject();
        }

        if(specialCompletions.size() != 0) completions.addAll(specialCompletions);

        return completions;
    }

    private boolean isCompletion(String str) {
        return !(str.startsWith("[") && str.endsWith("]"));
    }

    private boolean isSpecialCompletion(String str) {
        return str.startsWith("<") && str.endsWith(">");
    }

    private List<String> getSpecialCompletions(SmartCommand command, String key) {

        if(key.equals("<player>")) {

            List<String> players = new ArrayList<>();

            for(Player player : Bukkit.getOnlinePlayers()) players.add(player.getName());

            return players;
        }
        return command.getCompletions(key);
    }
}
