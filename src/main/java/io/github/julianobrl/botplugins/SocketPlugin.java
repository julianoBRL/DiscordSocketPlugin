package io.github.julianobrl.botplugins;

import io.github.julianobrl.botplugins.commands.socket.GetGuildsCommand;
import io.github.julianobrl.botplugins.commands.socket.GetSelfInfoCommand;
import io.github.julianobrl.botplugins.commands.socket.GetStatusCommand;
import io.github.julianobrl.botplugins.commands.socket.SetActivityCommand;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Plugin;

@Slf4j
public class SocketPlugin extends Plugin {

    private SocketServer socketServer;

    @Override
    public void start() {
        log.info("SocketPlugin-Start");

        socketServer = new SocketServer();
        socketServer.addCommandExecutor("SELF", new GetSelfInfoCommand());
        socketServer.addCommandExecutor("GET_STATUS", new GetStatusCommand());
        socketServer.addCommandExecutor("SET_ACTIVITY", new SetActivityCommand());
        socketServer.addCommandExecutor("SET_GUILDS", new GetGuildsCommand());
        socketServer.start();
    }

    @Override
    public void stop() {
        log.info("SocketPlugin-Stop");
        if (socketServer != null) {
            socketServer.stop();
        }
    }

    @Override
    public void delete() {
        log.info("SocketPlugin-Delete");
        if (socketServer != null) {
            socketServer.delete();
        }
    }

}