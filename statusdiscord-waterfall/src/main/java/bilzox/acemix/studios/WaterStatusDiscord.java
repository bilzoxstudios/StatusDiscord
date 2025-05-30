package bilzox.acemix.studios;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Created by Acemix
 * Project: StatusDiscord
 * Date: 20/5/2025 @ 04:58
 */
public class WaterStatusDiscord extends Plugin {

    private DiscordManager discordManager;
    private static WaterStatusDiscord instance;

    @Override
    public void onEnable() {
        int pluginId = 25923;
        Metrics metrics = new Metrics(this, pluginId);

        instance = this;
        Configuration config = loadConfig();
        discordManager = new DiscordManager(config);
        discordManager.startBot();

        discordManager.updateStatus(true);
        discordManager.updatePlayers(getProxy().getOnlineCount(), getProxy().getConfig().getPlayerLimit());

        getProxy().getPluginManager().registerListener(this, new PlayerEventListener(discordManager));
    }

    @Override
    public void onDisable() {
        discordManager.updateStatus(false);
        discordManager.shutdown();
    }

    private Configuration loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getDataFolder().mkdirs();
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load config.yml", e);
        }
    }

    public static WaterStatusDiscord getInstance() {
        return instance;
    }
}
