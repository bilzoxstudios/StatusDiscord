package bilzox.acemix.studios;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created by Acemix
 * Project: StatusDiscord
 * Date: 20/5/2025 @ 14:28
 */
public class DiscordManager {

    private final String token, guildId, statusChannelId, playersChannelId, nameServer;
    private final String statusOn, statusOff, playersOn, playersOff;
    private JDA jda;

    public DiscordManager(FileConfiguration config) {
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
            Bukkit.getConsoleSender().sendMessage("[StatusDiscord] ERROR: The token format is invalid. Please check your config.");
            return;
        }

        try {
            jda = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing(nameServer))
                    .build()
                    .awaitReady();
            Bukkit.getConsoleSender().sendMessage("[StatusDiscord] Bot connected successfully to Discord.");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("[StatusDiscord] Failed to start the Discord bot:");
            Bukkit.getConsoleSender().sendMessage(e.getClass().getSimpleName() + ": " + e.getMessage());
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
        Bukkit.getConsoleSender().sendMessage("===================================================");
        Bukkit.getConsoleSender().sendMessage("[StatusDiscord] CONFIGURATION ERROR");
        Bukkit.getConsoleSender().sendMessage("Missing or invalid config entries:");
        if (token == null || token.isEmpty()) Bukkit.getConsoleSender().sendMessage(" - modules.token is missing");
        if (guildId == null || guildId.isEmpty()) Bukkit.getConsoleSender().sendMessage(" - modules.guild is missing");
        if (nameServer == null || nameServer.isEmpty()) Bukkit.getConsoleSender().sendMessage(" - modules.nameserver is missing");
        if (statusChannelId == null || statusChannelId.isEmpty()) Bukkit.getConsoleSender().sendMessage(" - discord.status.channel is missing");
        if (playersChannelId == null || playersChannelId.isEmpty()) Bukkit.getConsoleSender().sendMessage(" - discord.players.channel is missing");
        if (statusOn == null || statusOff == null) Bukkit.getConsoleSender().sendMessage(" - discord.status.type.onenable/ondisable are missing");
        if (playersOn == null || playersOff == null) Bukkit.getConsoleSender().sendMessage(" - discord.players.type.onenable/ondisable are missing");
        Bukkit.getConsoleSender().sendMessage("Bot will not start until config is fixed.");
        Bukkit.getConsoleSender().sendMessage("===================================================");
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