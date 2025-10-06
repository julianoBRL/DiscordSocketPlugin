package io.github.julianobrl.botplugins.commands.socket;

import io.github.julianobrl.botplugins.dtos.GuildDto;
import io.github.julianobrl.botplugins.interfaces.IExecuteSocketCommands;
import io.github.julianobrl.discordbots.framework.managers.JDAManager;

public class GetGuildsCommand implements IExecuteSocketCommands {

    @Override
    public Object execute(Object data) {
        return  JDAManager.getInstance().getJda().getGuilds().stream()
                .map(GuildDto::fromGuild)
                .toList();
    }

}
