package i.solonin.configmanager.service.connect;

import i.solonin.configmanager.model.master.Device;

import java.util.List;

public interface TerminalService {
    void execute(String commands, List<Device> devices);

    Device get(Long id);
}
