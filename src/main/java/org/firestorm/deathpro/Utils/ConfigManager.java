package org.firestorm.deathpro.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.firestorm.deathpro.DeathPro;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private FileConfiguration config;
    private File configFile;
    public void setupConfig() {
        configFile = new File(DeathPro.getInstance().getDataFolder(), "config.yml");

        // Create the data folder if it doesn't exist
        if (!DeathPro.getInstance().getDataFolder().exists()) {
            DeathPro.getInstance().getDataFolder().mkdir();
        }

        DeathPro.getInstance().getLogger().info("Data folder path: " + DeathPro.getInstance().getDataFolder().getAbsolutePath());

        if (!configFile.exists()) {
            // Copy the default config from resources
            try (InputStream defaultConfigStream = DeathPro.getInstance().getResource("config.yml")) {
                if (defaultConfigStream != null && defaultConfigStream.available() > 0) {
                    DeathPro.getInstance().getLogger().info("Copying default config from resources...");
                    Files.copy(defaultConfigStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    DeathPro.getInstance().getLogger().info("Default config copied successfully!");
                } else {
                    DeathPro.getInstance().getLogger().warning("Default config.yml not found in resources or is empty!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = new YamlConfiguration(); // Use a custom YamlConfiguration instance
        try {
            config.load(configFile);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
        DeathPro.getInstance().getLogger().info("Config loaded from: " + configFile.getAbsolutePath());
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            setupConfig();
        }
        return config;
    }

    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            getConfig().save(configFile);
            DeathPro.getInstance().getLogger().warning("Config saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(DeathPro.getInstance().getDataFolder(), "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public String getDeathMessage() {
        List<String> deathMessages = config.getStringList("death-messages");
        int randomIndex = (int) (Math.random() * deathMessages.size());
        return deathMessages.get(randomIndex);
    }

    public String getKillerMessage() {
        List<String> killerMessages = config.getStringList("killer-messages");
        int randomIndex = (int) (Math.random() * killerMessages.size());
        return killerMessages.get(randomIndex);
    }

    public String getPrefix() {
        return config.getString("settings.prefix");
    }

    public String replacePlaceholders(String message, String playerName) {
        return replacePlaceholders(message, playerName, null, null, null);
    }

    public String replacePlaceholders(String message, String playerName, String killerName) {
        return replacePlaceholders(message, playerName, killerName, null, null);
    }

    public String replaceCoordinate(String message, String coordinate) {
        return replacePlaceholders(message, null, null, null, coordinate);
    }

    public String replacePlayerCoordinate(String message, String playerName, String coordinate) {
        return replacePlaceholders(message, playerName, null, null, coordinate);
    }

    public String replacePlaceholders(String message, String playerName, String killerName, String coordinate) {
        return replacePlaceholders(message, playerName, killerName, null, coordinate);
    }

    public String replacePlaceholders(String message, Integer countdown) {
        return replacePlaceholders(message, null, null, countdown, null);
    }

    public String replacePlaceholders(String message, String playerName, String killerName, Integer countdown, String coordinate) {
        if (message == null) {
            return null;
        }

        Map<String, String> placeholders = new HashMap<>();
        if (playerName != null) {
            placeholders.put("%player%", playerName);
        }
        if (killerName != null) {
            placeholders.put("%killer%", killerName);
        }
        if (countdown != null) {
            placeholders.put("%respawncountdown%", String.valueOf(countdown));
        }
        if (coordinate != null) {
            placeholders.put("%coordinate%", coordinate);
        }

        StringBuilder result = new StringBuilder(message);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            int start;
            while ((start = result.indexOf(entry.getKey())) != -1) {
                result.replace(start, start + entry.getKey().length(), entry.getValue());
            }
        }

        return result.toString();
    }
}