package io.github.julianobrl.botplugins;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.julianobrl.botplugins.dtos.requests.CommandRequest;
import io.github.julianobrl.botplugins.interfaces.IExecuteSocketCommands;
import io.github.julianobrl.botplugins.singletons.SingleObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class SocketServer {

    private static final String SOCKET_PATH = "./data/bot.socket";

    private final Map<String, IExecuteSocketCommands> commandExecutors = new HashMap<>();
    private ServerSocketChannel serverChannel; // Usamos ServerSocketChannel para Socket
    private Thread serverThread;
    private volatile boolean running = false;
    private final Path socketFile = Paths.get(SOCKET_PATH);

    public void addCommandExecutor(String cmd, IExecuteSocketCommands executor) {
        commandExecutors.put(cmd.toUpperCase(), executor);
        log.info("Command '{}' added to socket server.", cmd);
    }

    public void start() {
        Path dataDir = socketFile.getParent();

        try {
            // 1. Cria o diretório 'data' se não existir
            if (dataDir != null && Files.notExists(dataDir)) {
                Files.createDirectories(dataDir);
                log.info("Directory '{}' created.", dataDir);
            }

            // 2. Garante que o arquivo de socket anterior seja excluído antes do bind
            Files.deleteIfExists(socketFile);

            // 3. Inicia a thread do servidor
            running = true;
            serverThread = new Thread(this::runServerLogic, "SocketServer-Thread");
            serverThread.start();
            log.info("Socket server starting on path: {}", SOCKET_PATH);

        } catch (IOException e) {
            log.error("Error while preparing directories for Socket server: {}", e.getMessage(), e);
            running = false;
        }
    }

    public void stop() {
        running = false;
        log.info("Stopping Socket server...");

        if (serverChannel != null && serverChannel.isOpen()) {
            try {
                serverChannel.close(); // Isso irá forçar o serverChannel.accept() a sair
            } catch (IOException e) {
                log.error("Error while closing ServerSocketChannel.", e);
            }
        }

        if (serverThread != null) {
            serverThread.interrupt();
            try {
                serverThread.join(5000); // Espera a thread terminar por 5s
            } catch (InterruptedException e) {
                log.warn("Waiting for server thread to finish has been interrupted.", e);
                Thread.currentThread().interrupt();
            }
        }
        log.info("Socket server stopped.");
    }

    public void delete() {
        log.info("SocketPlugin-Delete. Removing Socket file...");
        try {
            if (serverChannel != null && serverChannel.isOpen()) {
                serverChannel.close(); // Fecha antes de deletar, por garantia
            }
            Files.deleteIfExists(socketFile);
            log.info("Socket socket file '{}' removed.", socketFile);
        } catch (IOException e) {
            log.error("Error deleting Socket socket file: {}", e.getMessage(), e);
        }
    }

    private void runServerLogic() {
        try {
            // 1. Cria o endereço Socket
            UnixDomainSocketAddress address = UnixDomainSocketAddress.of(socketFile);

            // 2. Abre e faz o bind do ServerSocketChannel
            serverChannel = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
            serverChannel.bind(address);
            serverChannel.configureBlocking(true); // Modo bloqueante para o accept()

            Set<PosixFilePermission> perms = EnumSet.of(
                    PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE,
                    PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_EXECUTE,
                    PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_WRITE, PosixFilePermission.OTHERS_EXECUTE
            );

            Files.setPosixFilePermissions(socketFile, perms);

            log.info("Socket Socket Server is listening on the path {}.", SOCKET_PATH);

            // 3. Loop principal de aceitação de conexões
            while (running) {
                try (SocketChannel clientChannel = serverChannel.accept()) { // Bloqueia e espera por uma conexão
                    log.info("New Socket connection accepted.");

                    processClient(clientChannel);

                } catch (IOException e) {
                    if (running) {
                        // Erro I/O inesperado no accept()
                        log.error("Error accepting connection or processing Socket client.", e);
                        // Pequeno delay para evitar loop rápido em caso de erro persistente
                        TimeUnit.MILLISECONDS.sleep(100);
                    }
                    // Se o loop foi interrompido (running=false), a exceção é esperada
                }
            }
        } catch (IOException | InterruptedException e) {
            if (running) {
                log.error("Could not start ServerSocketChannel Socket at path {}.", SOCKET_PATH, e);
            }
        } finally {
            running = false; // Garante que a flag seja setada
            // O serverChannel é fechado no stop() ou no delete(), mas podemos fechar aqui também.
            log.info("Socket Socket Server stopped listening.");
            // Garante que o arquivo seja deletado ao sair
            try {
                Files.deleteIfExists(socketFile);
            } catch (IOException e) {
                log.error("Error deleting Socket file in finally: {}", e.getMessage());
            }
        }
    }

    private void processClient(SocketChannel clientChannel) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            int bytesRead = clientChannel.read(buffer);
            if (bytesRead > 0) {
                buffer.flip();
                String inputMessage = new String(buffer.array(), 0, bytesRead).trim();
                log.info("Socket message received!");

                String stringResponse = SingleObjectMapper.getInstance()
                        .getMapper().writeValueAsString(processCommand(inputMessage));

                ByteBuffer responseBuffer = ByteBuffer.wrap(stringResponse.getBytes());
                clientChannel.write(responseBuffer);
            }
            clientChannel.close();
        } catch (IOException e) {
            log.error("I/O error while processing Socket client.", e);
        }
    }

    private Object processCommand(String fullMessage) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CommandRequest command = mapper.readValue(fullMessage, CommandRequest.class);
        String cmd = command.getCommand().toUpperCase();

        IExecuteSocketCommands executor = commandExecutors.get(cmd);

        if (executor != null) {
            log.info("Executing command: '{}'", cmd);
            return executor.execute(command.getData());
        } else {
            log.warn("Unknown command received: {}", cmd);
            return "ERRO: Unknown command received: " + cmd;
        }
    }
}