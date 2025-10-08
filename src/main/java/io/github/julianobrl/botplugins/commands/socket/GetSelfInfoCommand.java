package io.github.julianobrl.botplugins.commands.socket;

import io.github.julianobrl.botplugins.dtos.reponses.SelfInfoResponse;
import io.github.julianobrl.botplugins.interfaces.IExecuteSocketCommands;
import io.github.julianobrl.discordbots.framework.managers.JDAManager;

public class GetSelfInfoCommand implements IExecuteSocketCommands {

    @Override
    public Object execute(Object data) {
        return SelfInfoResponse.fromSelfUser(
                JDAManager.getInstance().getJda().getSelfUser());
    }

}
