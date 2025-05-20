package bilzox.acemix.studios;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.ProxyServer;

/**
 * Created by Acemix
 * Project: StatusDiscord
 * Date: 20/5/2025 @ 05:05
 */
public class PlayerEventListener {

    private final DiscordManager discordManager;
    private final ProxyServer server;

    public PlayerEventListener(DiscordManager discordManager, ProxyServer server) {
        this.discordManager = discordManager;
        this.server = server;
    }

    @Subscribe
    public void onJoin(PostLoginEvent event) {
        discordManager.updatePlayers(server.getPlayerCount(), server.getConfiguration().getShowMaxPlayers());
    }

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        discordManager.updatePlayers(server.getPlayerCount() - 1, server.getConfiguration().getShowMaxPlayers());
    }
}