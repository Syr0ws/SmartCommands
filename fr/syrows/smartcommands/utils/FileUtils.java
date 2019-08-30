package fr.syrows.smartcommands.utils;

import fr.syrows.smartcommands.SmartCommandsAPI;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public class FileUtils {

    public static void createFileFromResource(SmartCommandsAPI api, Path path, String resourcePath, boolean override) {

        if(Files.exists(path) && !override) return;

        InputStream inputStream = api.getPlugin().getResource(resourcePath);

        if(inputStream == null)
            throw new NullPointerException(String.format("No resource found at '%s'.", resourcePath));

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {

            api.getLogger().log(Level.INFO, "Reading resource file...");

            StringBuilder sb = new StringBuilder();
            String l;

            while((l = reader.readLine()) != null)
                sb.append(l).append("\n");

            api.getLogger().log(Level.INFO, "Writing data...");

            BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()));

            writer.write(sb.toString());
            writer.flush();
            writer.close();

            api.getLogger().log(Level.INFO, String.format("File %s was created.", path.getFileName()));

        } catch (IOException e) {

            api.getLogger().log(Level.SEVERE, String.format("Cannot create file %s.", path.getFileName()));

            e.printStackTrace();
        }
    }

    public static void createDirectory(SmartCommandsAPI api, Path path) {

        if(Files.exists(path)) return;

        try {

            Files.createDirectory(path);

            api.getLogger().log(Level.INFO, String.format("Directory %s was created.", path.getFileName()));

        } catch (IOException e) {

            api.getLogger().log(Level.SEVERE, String.format("Cannot create directory %s.", path.getFileName()));

            e.printStackTrace();
        }
    }
}
