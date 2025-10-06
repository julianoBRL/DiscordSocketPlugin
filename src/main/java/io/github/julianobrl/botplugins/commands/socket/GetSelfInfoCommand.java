package io.github.julianobrl.botplugins.commands.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.julianobrl.botplugins.dtos.SelfInfoDto;
import io.github.julianobrl.botplugins.interfaces.IExecuteSocketCommands;
import io.github.julianobrl.discordbots.framework.managers.JDAManager;

public class GetSelfInfoCommand implements IExecuteSocketCommands {

    @Override
    public Object execute(Object data) {
        return SelfInfoDto.fromSelfUser(
                JDAManager.getInstance().getJda().getSelfUser());
    }

}
