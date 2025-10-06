package io.github.julianobrl.botplugins.commands.socket;

import io.github.julianobrl.botplugins.interfaces.IExecuteSocketCommands;
import io.github.julianobrl.discordbots.framework.managers.JDAManager;

public class GetStatusCommand implements IExecuteSocketCommands {

    @Override
    public Object execute(Object data) {
        return JDAManager.getInstance().getJda().getStatus().toString();
    }

}
