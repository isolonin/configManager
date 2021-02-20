package i.solonin.configmanager.model.check;

import i.solonin.configmanager.model.WithDate;
import i.solonin.configmanager.model.master.Device;

public interface Result extends WithDate {
    Device getDevice();

    ResultType getType();
}
