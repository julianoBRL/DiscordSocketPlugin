package io.github.julianobrl.botplugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.julianobrl.botplugins.dtos.CommandDTO;
import io.github.julianobrl.botplugins.interfaces.IExecuteSocketCommands;
import io.github.julianobrl.botplugins.singletons.SingleObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class SocketServer {

    private static final String SOCKET_PATH = "./data/bot.socket";

    private final Map<String, IExecuteSocketCommands> commandExecutors = new HashMap<>();
    private ServerSocketChannel serverChannel; // Usamos ServerSocketChannel para UDS
    private Thread serverThread;
    private volatile boolean running = false;
    private final Path socketFile = Paths.get(SOCKET_PATH);

    public void addCommandExecutor(String cmd, IExecuteSocketCommands executor) {
        commandExecutors.put(cmd.toUpperCase(), executor);
        log.info("Comando '{}' adicionado ao servidor socket.", cmd);
    }

    public void start() {
        Path dataDir = socketFile.getParent();

        try {
            // 1. Cria o diretório 'data' se não existir
            if (dataDir != null && Files.notExists(dataDir)) {
                Files.createDirectories(dataDir);
                log.info("Diretório '{}' criado.", dataDir);
            }

            // 2. Garante que o arquivo de socket anterior seja excluído antes do bind
            Files.deleteIfExists(socketFile);

            // 3. Inicia a thread do servidor
            running = true;
            serverThread = new Thread(this::runServerLogic, "SocketServer-Thread");
            serverThread.start();
            log.info("Servidor UDS será iniciado. Caminho: {}", SOCKET_PATH);

        } catch (IOException e) {
            log.error("Erro ao preparar diretório para UDS: {}", e.getMessage(), e);
            running = false;
        }
    }

    public void stop() {
        running = false;
        log.info("Parando Servidor UDS...");

        if (serverChannel != null && serverChannel.isOpen()) {
            try {
                serverChannel.close(); // Isso irá forçar o serverChannel.accept() a sair
            } catch (IOException e) {
                log.error("Erro ao fechar ServerSocketChannel.", e);
            }
        }

        if (serverThread != null) {
            serverThread.interrupt();
            try {
                serverThread.join(5000); // Espera a thread terminar por 5s
            } catch (InterruptedException e) {
                log.warn("A espera pelo término da thread do servidor foi interrompida.", e);
                Thread.currentThread().interrupt();
            }
        }
        log.info("Servidor UDS parado.");
    }

    public void delete() {
        log.info("SocketPlugin-Delete. Removendo arquivo UDS...");
        try {
            if (serverChannel != null && serverChannel.isOpen()) {
                serverChannel.close(); // Fecha antes de deletar, por garantia
            }
            Files.deleteIfExists(socketFile);
            log.info("Arquivo de socket UDS '{}' removido.", socketFile);
        } catch (IOException e) {
            log.error("Erro ao deletar arquivo de socket UDS: {}", e.getMessage(), e);
        }
    }

    private void runServerLogic() {
        try {
            // 1. Cria o endereço UDS
            UnixDomainSocketAddress address = UnixDomainSocketAddress.of(socketFile);

            // 2. Abre e faz o bind do ServerSocketChannel
            serverChannel = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
            serverChannel.bind(address);
            serverChannel.configureBlocking(true); // Modo bloqueante para o accept()

            log.info("Servidor Socket UDS está escutando no arquivo {}.", SOCKET_PATH);

            // 3. Loop principal de aceitação de conexões
            while (running) {
                try (SocketChannel clientChannel = serverChannel.accept()) { // Bloqueia e espera por uma conexão
                    log.info("Nova conexão UDS aceita.");

                    processClient(clientChannel);

                } catch (IOException e) {
                    if (running) {
                        // Erro I/O inesperado no accept()
                        log.error("Erro ao aceitar conexão ou processar cliente UDS.", e);
                        // Pequeno delay para evitar loop rápido em caso de erro persistente
                        TimeUnit.MILLISECONDS.sleep(100);
                    }
                    // Se o loop foi interrompido (running=false), a exceção é esperada
                }
            }
        } catch (IOException | InterruptedException e) {
            if (running) {
                log.error("Não foi possível iniciar o ServerSocketChannel UDS no arquivo {}.", SOCKET_PATH, e);
            }
        } finally {
            running = false; // Garante que a flag seja setada
            // O serverChannel é fechado no stop() ou no delete(), mas podemos fechar aqui também.
            log.info("Servidor Socket UDS parou de escutar.");
            // Garante que o arquivo seja deletado ao sair
            try {
                Files.deleteIfExists(socketFile);
            } catch (IOException e) {
                log.error("Erro ao deletar arquivo UDS no finally: {}", e.getMessage());
            }
        }
    }

    private void processClient(SocketChannel clientChannel) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            // 1. Lendo os dados do canal
            int bytesRead = clientChannel.read(buffer);
            if (bytesRead > 0) {
                buffer.flip();
                String inputMessage = new String(buffer.array(), 0, bytesRead).trim();
                log.info("Mensagem UDS recebida: {}", inputMessage);

                // 2. Processa a mensagem no formato "COMANDO:PAYLOAD"
                String stringResponse = SingleObjectMapper.getInstance()
                        .getMapper().writeValueAsString(processCommand(inputMessage));

                // 3. Envia a resposta de volta ao cliente
                ByteBuffer responseBuffer = ByteBuffer.wrap(stringResponse.getBytes());
                clientChannel.write(responseBuffer);
            }
            clientChannel.close();
        } catch (IOException e) {
            log.error("Erro de I/O ao processar cliente UDS.", e);
        }
    }

    private Object processCommand(String fullMessage) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CommandDTO command = mapper.readValue(fullMessage, CommandDTO.class);
        String cmd = command.getCommand().toUpperCase();

        IExecuteSocketCommands executor = commandExecutors.get(cmd);

        if (executor != null) {
            log.info("Executando comando: '{}'", cmd);
            return executor.execute(command.getData());
        } else {
            log.warn("Comando desconhecido recebido: {}", cmd);
            return "ERRO: Comando desconhecido: " + cmd;
        }
    }
}