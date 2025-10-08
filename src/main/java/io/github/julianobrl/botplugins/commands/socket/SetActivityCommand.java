package io.github.julianobrl.botplugins.commands.socket;

import io.github.julianobrl.botplugins.interfaces.CustomActivityImpl;
import io.github.julianobrl.botplugins.dtos.requests.SetActivityRequest;
import io.github.julianobrl.botplugins.interfaces.IExecuteSocketCommands;
import io.github.julianobrl.botplugins.singletons.SingleObjectMapper;
import io.github.julianobrl.discordbots.framework.managers.JDAManager;

public class SetActivityCommand implements IExecuteSocketCommands {

    @Override
    public Object execute(Object data) {
        SetActivityRequest activity = SingleObjectMapper.getInstance()
                .getMapper().convertValue(data, SetActivityRequest.class);
        JDAManager.getInstance().getJda().getPresence().setActivity(
            new CustomActivityImpl(
                    activity.getName(),
                    null,
                    activity.getType()
            )
        );
        return "OK";
    }

}
