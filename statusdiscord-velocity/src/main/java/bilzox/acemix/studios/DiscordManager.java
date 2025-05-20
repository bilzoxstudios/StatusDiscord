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
        try {
            jda = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing(nameserver))
                    .build()
                    .awaitReady();
        } catch (Exception e) {
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
}