package bilzox.acemix.studios;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
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
        try {
            jda = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing(nameServer))
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
