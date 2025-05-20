package bilzox.acemix.studios;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Properties;
import java.util.logging.Logger;

@Plugin(id = "statusdiscord", name = "StatusDiscord", version = "1.0", authors = {"Acemix"})
public class VelocityStatusDiscord {

    private final Logger logger;
    private final ProxyServer server;
    private final Path dataDirectory;
    private final Metrics.Factory metricsFactory;

    private DiscordManager discordManager;

    @Inject
    public VelocityStatusDiscord(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        int pluginId = 25924;
        Metrics metrics = metricsFactory.make(this, pluginId);

        Path configPath = dataDirectory.resolve("config.properties");
        Properties config = loadConfig(configPath);

        discordManager = new DiscordManager(config);
        discordManager.startBot();

        discordManager.updateStatus(true);
        discordManager.updatePlayers(server.getPlayerCount(), server.getConfiguration().getShowMaxPlayers());

        server.getEventManager().register(this, new PlayerEventListener(discordManager, server));
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        discordManager.updateStatus(false);
        discordManager.shutdown();
    }

    private Properties loadConfig(Path path) {
        Properties props = new Properties();

        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                try (InputStream in = getClass().getResourceAsStream("/config.properties")) {
                    if (in != null) Files.copy(in, path);
                    else logger.warning("config.properties no encontrado en recursos");
                }
            }

            try (InputStream in = Files.newInputStream(path)) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error cargando config.properties", e);
        }

        return props;
    }
}