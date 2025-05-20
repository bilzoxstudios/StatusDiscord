package bilzox.acemix.studios;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Acemix
 * Project: StatusDiscord
 * Date: 20/5/2025 @ 14:28
 */
public class PlayerJoinLeaveListener implements Listener {

    private final DiscordManager discordManager;

    public PlayerJoinLeaveListener(DiscordManager manager) {
        this.discordManager = manager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        discordManager.updatePlayers(Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        discordManager.updatePlayers(Bukkit.getOnlinePlayers().size() - 1, Bukkit.getMaxPlayers());
    }
}