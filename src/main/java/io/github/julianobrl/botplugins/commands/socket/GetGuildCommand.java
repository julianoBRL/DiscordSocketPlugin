package io.github.julianobrl.botplugins.commands.socket;

import io.github.julianobrl.botplugins.dtos.reponses.GuildResponse;
import io.github.julianobrl.botplugins.dtos.reponses.GuildsResponse;
import io.github.julianobrl.botplugins.interfaces.IExecuteSocketCommands;
import io.github.julianobrl.botplugins.singletons.SingleObjectMapper;
import io.github.julianobrl.discordbots.framework.managers.JDAManager;
import net.dv8tion.jda.api.entities.Guild;

public class GetGuildCommand implements IExecuteSocketCommands {

    @Override
    public Object execute(Object data) {
        String guildId = SingleObjectMapper.getInstance().getMapper().convertValue(data, String.class);
        Guild guild = JDAManager.getInstance().getJda().getGuildById(guildId);
        if(guild == null) return "Error: guild not found!";
        return GuildResponse.fromGuild(guild);
    }

}
