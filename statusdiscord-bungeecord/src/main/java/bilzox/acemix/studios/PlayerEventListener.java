package bilzox.acemix.studios;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Acemix
 * Project: StatusDiscord
 * Date: 20/5/2025 @ 04:59
 */
public class PlayerEventListener implements Listener {

    private final DiscordManager discordManager;

    public PlayerEventListener(DiscordManager manager) {
        this.discordManager = manager;
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        discordManager.updatePlayers(ProxyServer.getInstance().getOnlineCount(),
                ProxyServer.getInstance().getConfig().getPlayerLimit());
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        discordManager.updatePlayers(ProxyServer.getInstance().getOnlineCount() - 1,
                ProxyServer.getInstance().getConfig().getPlayerLimit());
    }
}
