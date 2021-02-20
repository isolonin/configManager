package i.solonin.configmanager.service.connect;

import i.solonin.configmanager.model.check.ExecutionResult;
import i.solonin.configmanager.model.check.ResultType;
import i.solonin.configmanager.model.master.Device;
import i.solonin.configmanager.model.terminal.Terminal;
import i.solonin.configmanager.service.repos.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TerminalServiceImpl implements TerminalService {
    private final DeviceRepository deviceRepository;
    @Autowired
    private TerminalService self;

    @Override
    public void execute(String commands, List<Device> devices) {
        devices.forEach(d -> {
            Device device = self.get(d.getId());
            ExecutionResult executionResult = new ExecutionResult(device);
            device.getExecutions().add(executionResult);
            try {
                Terminal terminal = new Terminal();
                terminal.login(device);
                Arrays.stream(commands.split("\n")).forEach(cmd -> {
                    try {
                        String result = terminal.send(cmd);
                        executionResult.getResults().put(cmd, result);
                        executionResult.setType(ResultType.COMPLETED);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        executionResult.setType(ResultType.COMMAND_FAILED);
                        executionResult.getResults().put(cmd, e.getMessage());
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage());
                executionResult.setType(ResultType.LOGIN_FAILED);
            }
            deviceRepository.saveAndFlush(device);
        });
    }

    @Override
    @Transactional
    public Device get(Long id) {
        return deviceRepository.findById(id).map(d -> {
            Hibernate.initialize(d.getExecutions());
            return d;
        }).orElse(null);
    }
}
