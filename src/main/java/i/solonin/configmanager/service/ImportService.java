package i.solonin.configmanager.service;

import i.solonin.configmanager.model.Device;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.primefaces.model.file.UploadedFile;

import java.util.List;

public interface ImportService {
    List<Device> getDevicesByExcel(UploadedFile file) throws InvalidFormatException;

    void importDevices(List<Device> devices);
}
