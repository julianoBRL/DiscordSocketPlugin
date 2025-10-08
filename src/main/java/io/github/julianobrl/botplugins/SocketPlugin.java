package io.github.julianobrl.botplugins;

import io.github.julianobrl.botplugins.commands.socket.*;
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
        socketServer.addCommandExecutor("GET_GUILDS", new GetGuildsCommand());
        socketServer.addCommandExecutor("GET_GUILD", new GetGuildCommand());
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