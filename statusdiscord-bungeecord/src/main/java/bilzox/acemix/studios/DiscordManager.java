package bilzox.acemix.studios;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

/**
 * Created by Acemix
 * Project: StatusDiscord
 * Date: 20/5/2025 @ 04:59
 */
public class DiscordManager {

    private final String token, guildId, statusChannelId, playersChannelId, nameServer;
    private final String statusOn, statusOff, playersOn, playersOff;
    private JDA jda;

    public DiscordManager(Configuration config) {
        token = config.getString("modules.token");
        guildId = config.getString("modules.guild");
        nameServer = config.getString("modules.nameserver");
        statusChannelId = config.getString("discord.status.channel");
        playersChannelId = config.getString("discord.players.channel");

        statusOn = config.getString("discord.status.type.onenable");
        statusOff = config.getString("discord.status.type.ondisable");
        playersOn = config.getString("discord.players.type.onenable");
        playersOff = config.getString("discord.players.type.ondisable");
    }

    public void startBot() {
        if (!isValidConfig()) {
            logConfigError();
            return;
        }

        if (!isTokenValid(token)) {
            ProxyServer.getInstance().getLogger().severe("[StatusDiscord] ERROR: Invalid token format. Check your config.");
            return;
        }

        try {
            jda = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing(nameServer))
                    .build()
                    .awaitReady();
            ProxyServer.getInstance().getLogger().info("[StatusDiscord] Bot connected successfully to Discord.");
        } catch (Exception e) {
            ProxyServer.getInstance().getLogger().severe("[StatusDiscord] Failed to start the Discord bot:");
            ProxyServer.getInstance().getLogger().severe(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private boolean isValidConfig() {
        return token != null && !token.isEmpty()
                && guildId != null && !guildId.isEmpty()
                && nameServer != null && !nameServer.isEmpty()
                && statusChannelId != null && !statusChannelId.isEmpty()
                && playersChannelId != null && !playersChannelId.isEmpty()
                && statusOn != null && statusOff != null
                && playersOn != null && playersOff != null;
    }

    private boolean isTokenValid(String token) {
        return token != null && token.split("\\.").length == 3;
    }

    private void logConfigError() {
        ProxyServer.getInstance().getLogger().severe("===================================================");
        ProxyServer.getInstance().getLogger().severe("[StatusDiscord] CONFIGURATION ERROR");
        ProxyServer.getInstance().getLogger().severe("Missing or invalid config entries:");
        if (token == null || token.isEmpty()) ProxyServer.getInstance().getLogger().severe(" - modules.token is missing");
        if (guildId == null || guildId.isEmpty()) ProxyServer.getInstance().getLogger().severe(" - modules.guild is missing");
        if (nameServer == null || nameServer.isEmpty()) ProxyServer.getInstance().getLogger().severe(" - modules.nameserver is missing");
        if (statusChannelId == null || statusChannelId.isEmpty()) ProxyServer.getInstance().getLogger().severe(" - discord.status.channel is missing");
        if (playersChannelId == null || playersChannelId.isEmpty()) ProxyServer.getInstance().getLogger().severe(" - discord.players.channel is missing");
        if (statusOn == null || statusOff == null)
            ProxyServer.getInstance().getLogger().severe(" - discord.status.type.onenable/ondisable are missing");
        if (playersOn == null || playersOff == null)
            ProxyServer.getInstance().getLogger().severe(" - discord.players.type.onenable/ondisable are missing");
        ProxyServer.getInstance().getLogger().severe("Bot will not start until config is fixed.");
        ProxyServer.getInstance().getLogger().severe("===================================================");
    }

    public void updateStatus(boolean enabled) {
        String name = enabled ? statusOn : statusOff;
        updateChannelName(statusChannelId, name);
    }

    public void updatePlayers(int online, int max) {
        String name = playersOn.replace("{online}", String.valueOf(online))
                .replace("{max}", String.valueOf(max));
        updateChannelName(playersChannelId, name);
    }

    private void updateChannelName(String channelId, String name) {
        if (jda == null) return;
        Guild guild = jda.getGuildById(guildId);
        if (guild == null || guild.getVoiceChannelById(channelId) == null) return;
        guild.getVoiceChannelById(channelId).getManager().setName(name).queue();
    }

    public void shutdown() {
        if (jda != null) jda.shutdownNow();
    }
}