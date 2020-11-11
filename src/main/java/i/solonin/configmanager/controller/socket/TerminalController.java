package i.solonin.configmanager.controller.socket;

import i.solonin.configmanager.model.Device;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;
import org.apache.commons.net.SocketClient;
import org.apache.commons.net.telnet.TelnetClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.inject.Named;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Controller
@Named("terminalController")
@Getter
public class TerminalController {
    private TelnetClient telnet = null;
    private String terminalStatus = "Не подключено";

    @Autowired
    private SimpMessagingTemplate webSocket;

    public void connect(Device device) {
        if (Optional.ofNullable(telnet).map(SocketClient::isConnected).orElse(false)) {
            log.warn("terminal already connected and will be closed: {}", telnet);
            close();
        }
        CompletableFuture.runAsync(() -> {
            try {
                terminalStatus = "Идёт подключение к устройству...";
                telnet = new TelnetClient();
                telnet.setConnectTimeout(5000);
                telnet.connect(device.getHost());
                webSocket.convertAndSend("/client/update-terminal-connection", true);
                terminalStatus = "Подключено";
                log.info("successfully connected to {}", terminal());
            } catch (Exception e) {
                log.error(e.getMessage());
                webSocket.convertAndSend("/client/update-terminal-connection", false);
                terminalStatus = String.format("Не удалось соедениться: %s", e.getMessage());
            }
        });
    }

    public void close() {
        if (telnet == null) return;
        try {
            terminalStatus = "Соединение разорвано";
            log.info("close connection {}", terminal());
            telnet.disconnect();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public String handleCommand(String command, String[] params) {
        if (Optional.ofNullable(telnet).map(t -> !t.isConnected()).orElse(true)) return "Соединение не установлено";
        try {
            String send = String.join(" ", command, String.join(" ", params));
            log.info("{}", send);
            StringBuilder buffer = new StringBuilder();
            Expect expect = new ExpectBuilder()
                    .withOutput(telnet.getOutputStream())
                    .withInputs(telnet.getInputStream())
                    .withEchoOutput(buffer)
                    .withEchoInput(buffer)
                    .withExceptionOnFailure()
                    .build();

            expect.sendLine(send);
            return buffer.toString();
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
        if (telnet != null)
            close();
    }

    private String terminal() {
        if (telnet.getRemoteAddress() != null)
            return Optional.ofNullable(telnet)
                    .map(t -> String.format("%s:%d", telnet.getRemoteAddress().getHostAddress(), telnet.getRemotePort()))
                    .orElse(null);
        return "";
    }
}
