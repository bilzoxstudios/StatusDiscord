package bilzox.acemix.studios;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Acemix
 * Project: StatusDiscord
 * Date: 20/5/2025 @ 04:47
 */
public class SpigotStatusDiscord extends JavaPlugin {

    private static SpigotStatusDiscord instance;
    private DiscordManager discordManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        discordManager = new DiscordManager(getConfig());
        discordManager.startBot();

        discordManager.updateStatus(true);
        discordManager.updatePlayers(Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());

        getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(discordManager), this);
    }

    @Override
    public void onDisable() {
        discordManager.updateStatus(false);
        discordManager.shutdown();
    }

    public static SpigotStatusDiscord getInstance() {
        return instance;
    }
}
