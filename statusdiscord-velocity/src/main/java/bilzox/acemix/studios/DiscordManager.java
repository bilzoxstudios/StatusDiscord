package bilzox.acemix.studios;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Properties;

/**
 * Created by Acemix
 * Project: StatusDiscord
 * Date: 20/5/2025 @ 05:05
 */
public class DiscordManager {

    private final String token, guildId, statusChannelId, playersChannelId, nameserver;
    private final String statusOn, statusOff, playersOn, playersOff;
    private JDA jda;

    public DiscordManager(Properties config) {
        token = config.getProperty("modules.token");
        guildId = config.getProperty("modules.guild");
        nameserver = config.getProperty("modules.nameserver");

        statusChannelId = config.getProperty("discord.status.channel");
        playersChannelId = config.getProperty("discord.players.channel");

        statusOn = config.getProperty("discord.status.type.onenable");
        statusOff = config.getProperty("discord.status.type.ondisable");
        playersOn = config.getProperty("discord.players.type.onenable");
        playersOff = config.getProperty("discord.players.type.ondisable");
    }

    public void startBot() {
        if (!isValidConfig()) {
            logConfigError();
            return;
        }

        if (!isTokenValid(token)) {
            System.out.println("§c========================================");
            System.out.println("§c        [StatusDiscord] INVALID TOKEN  ");
            System.out.println("§c  The token provided is not valid.     ");
            System.out.println("§c  Please check your config.            ");
            System.out.println("§cBot will not attempt to connect.       ");
            System.out.println("§c========================================");
            return;
        }

        try {
            jda = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing(nameserver))
                    .build()
                    .awaitReady();
            System.out.println("§a[StatusDiscord] Bot successfully connected.");
        } catch (Exception e) {
            System.out.println("§c[StatusDiscord] Unexpected error while starting the bot:");
            e.printStackTrace();
        }
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
        if (guild == null) return;
        guild.getVoiceChannelById(channelId).getManager().setName(name).queue();
    }

    public void shutdown() {
        if (jda != null) jda.shutdownNow();
    }

    private boolean isValidConfig() {
        return token != null && !token.isEmpty()
                && guildId != null && !guildId.isEmpty()
                && statusChannelId != null && !statusChannelId.isEmpty()
                && playersChannelId != null && !playersChannelId.isEmpty()
                && nameserver != null && !nameserver.isEmpty()
                && statusOn != null && statusOff != null
                && playersOn != null && playersOff != null;
    }

    private void logConfigError() {
        System.out.println("§c========================================");
        System.out.println("§c      [StatusDiscord] CONFIG ERROR     ");
        System.out.println("§c One or more configuration fields are  ");
        System.out.println("§c missing or empty in the config file.  ");
        System.out.println("§c Please review your config settings.   ");
        System.out.println("§c========================================");
    }

    private boolean isTokenValid(String token) {
        return token != null && token.split("\\.").length == 3;
    }
}
