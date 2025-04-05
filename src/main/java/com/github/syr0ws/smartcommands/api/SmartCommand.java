package com.github.syr0ws.smartcommands.api;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.smartcommands.api.argument.CommandArgument;
import com.github.syr0ws.smartcommands.api.argument.CommandArgumentTree;
import com.github.syr0ws.smartcommands.api.argument.DynamicArgumentValueProvider;
import com.github.syr0ws.smartcommands.api.exception.CommandCallException;
import com.github.syr0ws.smartcommands.api.exception.InvalidCommandMethodException;
import com.github.syr0ws.smartcommands.internal.argument.DynamicCommandArgument;
import com.github.syr0ws.smartcommands.internal.argument.SimpleCommandArgumentTree;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Abstract base class for handling commands with dynamic routing and auto-completion.
 */
public abstract class SmartCommand implements CommandExecutor, TabCompleter {

    private final SimpleCommandArgumentTree tree = new SimpleCommandArgumentTree(this);

    /**
     * Configures the command by adding custom dynamic argument providers.
     */
    public abstract void configure();

    /**
     * Called when a command is not found. Implement this method to handle cases where the command sent by the player is invalid.
     *
     * @param sender the sender who executed the command
     * @param label  the command label used
     * @param args   the arguments passed with the command
     */
    public abstract void onCommandNotFound(CommandSender sender, String label, String[] args);

    /**
     * Called when the sender does not have the required {@link CommandSenderType} to execute the command.
     *
     * @param sender  the sender who executed the command
     * @param command the corresponding command
     */
    public abstract void onNotAllowedSenderType(CommandSender sender, Command command);

    /**
     * Called when the sender does not have the required permission to execute the command.
     *
     * @param sender  the sender who executed the command
     * @param command the corresponding command
     */
    public abstract void onNoPermission(CommandSender sender, Command command);

    /**
     * Returns the name of the command.
     *
     * @return the command's name
     */
    public abstract String getName();

    /**
     * @see CommandExecutor#onCommand(CommandSender, org.bukkit.command.Command, String, String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        this.call(sender, label, args);
        return true;
    }

    /**
     * @see TabCompleter#onTabComplete(CommandSender, org.bukkit.command.Command, String, String[])
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return this.getCompletions(sender, args);
    }

    /**
     * Builds the command. For internal use only.
     */
    public void build() {
        this.buildArgumentTree();
        this.configure();
    }

    /**
     * Builds the argument tree by scanning methods annotated with the {@link Command} annotation.
     */
    private void buildArgumentTree() {

        String commandName = this.getName();

        List<Method> methods = Arrays.stream(this.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Command.class))
                .toList();

        for (Method method : methods) {

            String methodName = method.getName();

            // Checking that the method has the following signature: methodName(CommandContext)
            if (method.getParameterCount() != 1) {
                throw new InvalidCommandMethodException("Method %s of command %s must have only one parameter of type %s".formatted(methodName, commandName, CommandExecutionContext.class.getName()));
            }

            Class<?> firstParamType = method.getParameterTypes()[0];

            if (!firstParamType.equals(CommandExecutionContext.class)) {
                throw new InvalidCommandMethodException("Method %s of command %s must have only one parameter of type %s".formatted(methodName, commandName, CommandExecutionContext.class.getName()));
            }

            // Retrieving and parsing the annotation.
            Command command = method.getAnnotation(Command.class);
            CommandCallable callable = new CommandCallable(this, command, method);

            this.tree.addCommandToTree(callable);
        }
    }

    /**
     * Executes the command based on the sender input.
     *
     * @param sender the sender who executed the command
     * @param label  the command label
     * @param args   the arguments passed with the command
     */
    public void call(CommandSender sender, String label, String[] args) {
        Validate.notNull(sender, "sender cannot be null");
        Validate.notEmpty(label, "label cannot be null or empty");
        Validate.notNull(args, "args cannot be null");

        Optional<CommandArgument> optionalCommandArgument = this.tree.getArgument(args);

        // Case in which no path can be matched with the given arguments.
        if (optionalCommandArgument.isEmpty()) {
            this.onCommandNotFound(sender, label, args);
            return;
        }

        CommandArgument argument = optionalCommandArgument.get();

        Optional<CommandCallable> optionalCommandCallable = argument.getCallable();

        // Case in which there is no method associated with the argument to execute a command.
        if (optionalCommandCallable.isEmpty()) {
            this.onCommandNotFound(sender, label, args);
            return;
        }

        CommandCallable callable = optionalCommandCallable.get();

        // Checking that the sender is allowed to execute the command.
        if (!callable.isAllowedSender(sender)) {
            this.onNotAllowedSenderType(sender, callable.command());
            return;
        }

        if (!callable.hasPermission(sender)) {
            this.onNoPermission(sender, callable.command());
            return;
        }

        // Invoking the command.
        Map<String, String> arguments = this.getArgumentValues(argument, args);
        CommandExecutionContext context = new CommandExecutionContext(sender, label, arguments);

        try {
            callable.call(context);
        } catch (CommandCallException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Build a map with argument name as keys and their corresponding value entered by the player as values.
     *
     * @param argument the argument corresponding to the command executed
     * @param args the arguments entered by the player
     * @return a map of arguments with their corresponding value
     */
    private Map<String, String> getArgumentValues(CommandArgument argument, String[] args) {

        String path = argument.getPath();
        String[] array = path.split("\\.");

        Map<String, String> arguments = new HashMap<>();

        for (int i = 0; i < args.length; i++) {

            // Using i+1 because the path contains the root (command name), which is not the case in args.
            String pathArgument = array[i+1];

            if(DynamicCommandArgument.isDynamicArgument(pathArgument)) {
                arguments.put(pathArgument, args[i]);
            }
        }

        return arguments;
    }

    /**
     * Provides tab completion suggestions based on the sender input.
     *
     * @param sender the sender requesting tab completion
     * @param args   the arguments passed with the command
     * @return a list of completion suggestions
     */
    public List<String> getCompletions(CommandSender sender, String[] args) {

        // We need to find the parent of the current argument to propose an autocomplete for the current argument.
        String[] argsWithoutLast = Arrays.copyOfRange(args, 0, args.length - 1);
        String lastArg = args[args.length - 1].toLowerCase();

        Optional<CommandArgument> optional = this.tree.getArgument(argsWithoutLast);

        // The path of arguments cannot be matched to a node.
        if (optional.isEmpty()) {
            return Collections.emptyList();
        }

        CommandArgument argument = optional.get();

        // Providing completions.
        List<String> completions = new ArrayList<>();

        for (CommandArgument child : argument.getChildren()) {

            // Checking that there is at least one command that the sender has access to in the child hierarchy
            // to provide autocompletion.
            Set<CommandCallable> callables = child.getChildCommands();

            boolean hasPermission = callables.stream()
                    .anyMatch(commandCallable -> commandCallable.canCall(sender));

            if (!hasPermission) {
                continue;
            }

            // Providing autocompletion if possible.
            if (child instanceof DynamicCommandArgument dynamicArgument) {
                this.addDynamicCompletions(sender, completions, dynamicArgument, lastArg);
            } else if (child.getName().startsWith(lastArg) && !completions.contains(child.getName())) {
                completions.add(child.getName());
            }
        }

        return completions;
    }

    /**
     * Adds dynamic completions for the given argument if a provider is available.
     *
     * @param sender      the sender requesting completion
     * @param completions the list of current completions
     * @param argument    the dynamic argument
     * @param inputArg    the current input argument
     */
    private void addDynamicCompletions(CommandSender sender, List<String> completions, DynamicCommandArgument argument, String inputArg) {

        Optional<DynamicArgumentValueProvider> optional = argument.getProvider();

        // The dynamic argument does not have any provider so not completions can be proposed.
        if (optional.isEmpty()) {
            return;
        }

        DynamicArgumentValueProvider provider = optional.get();

        List<String> values = provider.provide(sender);
        values.stream().filter(value -> !completions.contains(value) && value.startsWith(inputArg)).forEach(completions::add);
    }

    /**
     * Returns the command argument tree.
     *
     * @return the command argument tree
     */
    public CommandArgumentTree getTree() {
        return this.tree;
    }
}
