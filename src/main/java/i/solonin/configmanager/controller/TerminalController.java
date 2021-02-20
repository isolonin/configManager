package i.solonin.configmanager.controller;

import i.solonin.configmanager.model.master.Device;
import i.solonin.configmanager.model.terminal.Terminal;
import i.solonin.configmanager.service.connect.TerminalService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.SocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.inject.Named;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Controller
@Named("terminalController")
@Getter
public class TerminalController extends AbstractController {
    @Setter
    private String commands;
    private final Terminal terminal = new Terminal();
    private String terminalStatus = "Не подключено";

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private SimpMessagingTemplate webSocket;

    public void connect(Device device) {
        if (Optional.ofNullable(terminal.getTelnet()).map(SocketClient::isConnected).orElse(false)) {
            log.warn("terminal already connected and will be closed: {}", terminal.getTelnet());
            close();
        }
        CompletableFuture.runAsync(() -> {
            try {
                terminalStatus = "Идёт подключение к устройству...";
                terminal.login(device);
                terminalStatus = "Подключено";
                log.info("successfully connected to {}", terminal.print());
                webSocket.convertAndSend("/client/update-terminal-connection", true);
            } catch (Exception e) {
                log.error(e.getMessage());
                terminalStatus = String.format("Не удалось соедениться: %s", e.getMessage());
                webSocket.convertAndSend("/client/update-terminal-connection", false);
            }
        });
    }

    public void execute(List<Device> devices) {
        if (!StringUtils.isEmpty(commands)) {
            terminalService.execute(commands, devices);
            commands = null;
            devices.clear();
        } else {
            showErrorMessage("Для начала выполнения необходимо указать команды");
        }
    }

    public void close() {
        try {
            terminalStatus = "Соединение разорвано";
            log.info("close connection {}", terminal.print());
            terminal.disconnect();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public String handleCommand(String command, String[] params) {
        if (!terminal.isLogin()) return "Соединение не установлено";
        try {
            String cmd = String.join(" ", command, String.join(" ", params));
            String result = terminal.send(cmd);
            log.info("{}: {}", cmd, result);
            return result;
        } catch (Exception e) {
            close();
            log.error(e.getMessage());
            webSocket.convertAndSend("/client/update-terminal-connection", false);
            return e.getMessage();
        }
    }

    @EventListener(SessionConnectEvent.class)
    public void handleWebsocketConnectListner(SessionConnectEvent event) {
//        log.info("Received a new web socket connection: {}", event);
    }

    @EventListener(SessionDisconnectEvent.class)
    public void handleWebsocketDisconnectListner(SessionDisconnectEvent event) {
        close();
    }
}
