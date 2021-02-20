package i.solonin.configmanager.model.terminal;

import i.solonin.configmanager.model.master.Device;
import i.solonin.configmanager.model.master.EquipmentType;
import i.solonin.configmanager.model.master.Model;
import i.solonin.configmanager.model.master.Template;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;
import net.sf.expectit.Result;
import net.sf.expectit.matcher.Matcher;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static net.sf.expectit.matcher.Matchers.contains;

@Data
@Slf4j
@NoArgsConstructor
public class Terminal {
    private TelnetClient telnet;
    private Device device;
    private Expect expect;
    private StringBuilder buffer = new StringBuilder();
    private boolean isLogin;

    public void login(Device device) throws Exception {
        this.device = device;
        telnet = new TelnetClient();
        telnet.setConnectTimeout(5000);
        telnet.connect(device.getHost());

        Thread.currentThread().setName(telnet.getRemoteAddress() + ":" + telnet.getRemotePort());

        this.expect = new ExpectBuilder()
                .withOutput(telnet.getOutputStream())
                .withInputs(telnet.getInputStream())
                .withEchoOutput(buffer)
                .withEchoInput(buffer)
                .withTimeout(10, TimeUnit.SECONDS)
                .withExceptionOnFailure()
                .build();

        try {
            expect(contains(getMatchers(device, EquipmentType::getLoginMatchers)));
            sendLine(device.getLogin());
            expect(contains(getMatchers(device, EquipmentType::getPasswordMatchers)));
            sendLine(device.getPassword());
            expect(contains(getMatchers(device, EquipmentType::getPromptMatchers)));
        } catch (IOException e) {
            throw new Exception("ошибка аутентификации");
        }
        isLogin = true;
    }

    public void disconnect() throws IOException {
        if (telnet != null) {
            telnet.disconnect();
            isLogin = false;
        }
    }

    public String print() {
        if (telnet != null && telnet.getRemoteAddress() != null)
            return Optional.ofNullable(telnet)
                    .map(t -> String.format("%s:%d", telnet.getRemoteAddress().getHostAddress(), telnet.getRemotePort()))
                    .orElse(null);
        return "";
    }

    public Result expect(Matcher<Result> contains) throws IOException {
        return this.expect.expect(contains);
    }

    public void sendLine(String value) throws IOException {
        expect.sendLine(value);
    }

    private String getMatchers(Device device, Function<EquipmentType, String> function) {
        return Optional.ofNullable(device.getModel())
                .map(Model::getTemplate)
                .map(Template::getType)
                .map(function).orElse("login:");
    }

    public String send(String cmd) throws IOException {
        sendLine(cmd);
        String result = expect(contains(getMatchers(device, EquipmentType::getPromptMatchers)))
                .getInput().replaceFirst(cmd + "\r\n", "");
        log.info("send({}): {}", cmd, result);
        return result;
    }
}
