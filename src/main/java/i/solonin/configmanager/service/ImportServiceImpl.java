package i.solonin.configmanager.service;

import i.solonin.configmanager.model.*;
import i.solonin.configmanager.service.repos.DeviceRepository;
import i.solonin.configmanager.service.repos.ModelRepository;
import i.solonin.configmanager.service.repos.RegionRepository;
import i.solonin.configmanager.service.repos.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.primefaces.model.UploadedFile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportServiceImpl implements ImportService {
    private final String[] MANDATORY_HEADERS = {"SNMPAddress", "Vendor", "Model", "Name"};

    private final DeviceRepository deviceRepository;
    private final ModelRepository modelRepository;
    private final VendorRepository vendorRepository;
    private final RegionRepository regionRepository;

    @Override
    public List<Device> getDevicesByExcel(UploadedFile file) throws InvalidFormatException {
        List<Device> result = new ArrayList<>();
        try {
            List<Device> newDevices = read(file.getInputstream());
            List<Device> oldDevices = deviceRepository.findAll();
            result.addAll(oldDevices.stream()
                    .filter(oldDevice -> newDevices.stream().noneMatch(oldDevice::equalsByHost))
                    .peek(d -> d.setImportType(Device.ImportType.REMOVE))
                    .collect(Collectors.toList()));
            result.addAll(newDevices.stream().filter(newDevice -> oldDevices.stream().anyMatch(newDevice::equalsByHost))
                    .peek(d -> {
                        d.setOriginDevice(oldDevices.stream().filter(d::equalsByHost).findFirst().orElse(null));
                        if (d.isEqualsToOrigin())
                            d.setImportType(Device.ImportType.EQUALS);
                        else
                            d.setImportType(Device.ImportType.MODIFY);
                    }).collect(Collectors.toList()));
            result.addAll(newDevices.stream()
                    .filter(newDevice -> oldDevices.stream().noneMatch(newDevice::equalsByHost))
                    .peek(d -> d.setImportType(Device.ImportType.NEW))
                    .collect(Collectors.toList()));
            result.sort((o1, o2) -> {
                if(o1.getImportType().equals(o2.getImportType())) return 0;
                if (o1.getImportType().equals(Device.ImportType.REMOVE)) return -1;
                if (o2.getImportType().equals(Device.ImportType.REMOVE)) return 1;
                if (o1.getImportType().equals(Device.ImportType.EQUALS)) return 1;
                if (o2.getImportType().equals(Device.ImportType.EQUALS)) return -1;
                return o1.getImportType().compareTo(o2.getImportType());
            });
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new InvalidFormatException("Не удалось прочитать Excel файл");
        }
        return result;
    }

    @Override
    public void importDevices(List<Device> devices) {
        devices.forEach(d -> {
            switch (d.getImportType()) {
                case NEW:
                    save(d);
                    log.info("{} was created", d);
                    break;
                case REMOVE:
                    deviceRepository.delete(d);
                    log.info("{} was removed", d);
                    break;
                case MODIFY:
                    save(d);
                    log.info("{} was modify", d);
                    break;
            }
        });
    }

    private void save(Device device) {
        Vendor vendor = vendorRepository.getByNameOrCreate(device.getModel().getVendor().getName());
        Model model = modelRepository.getByNameAndVendorOrCreate(device.getModel().getName(), vendor);
        device.setModel(model);
        Device origin = device.getOriginDevice();
        if (device.getRegion() != null)
            device.setRegion(regionRepository.getByNameOrCreate(device.getRegion().getName()));
        device.setId(Optional.ofNullable(origin).map(DBEntity::getId).orElse(null));
        device.setLogin(Optional.ofNullable(origin).map(Device::getLogin).orElse(null));
        device.setPassword(Optional.ofNullable(origin).map(Device::getPassword).orElse(null));
        deviceRepository.save(device);
    }

    private List<Device> read(InputStream inputStream) throws Exception {
        List<Device> result = new ArrayList<>();
        Map<Integer, Type> types = new HashMap<>();
        Workbook workbook = WorkbookFactory.create(inputStream);
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getLastCellNum() < 0) continue;
                if (row.getRowNum() == 0) {
                    types = getHeaders(row);
                    continue;
                }
                result.add(getDevice(row, types));
            }
        }
        return result;
    }

    private Device getDevice(Row row, Map<Integer, Type> types) {
        Device result = new Device();
        String vendor = null;
        String model = null;
        String region = null;

        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            Type type = types.get(cell.getColumnIndex());
            if (type != null && cell.getCellType().equals(CellType.STRING)) {
                String value = cell.getStringCellValue();
                if (value.equalsIgnoreCase("NONE")) continue;

                switch (type) {
                    case HOST:
                        result.setHost(value);
                        break;
                    case VENDOR:
                        vendor = value;
                        break;
                    case MODEL:
                        model = value;
                        break;
                    case NAME:
                        result.setName(value);
                        break;
                    case REGION:
                        region = value;
                        break;
                    case LOCATION:
                        result.setLocation(value);
                        break;
                    case FIRMWARE:
                        result.setFirmware(value);
                        break;
                    case FIRMREVISION:
                        result.setFirmRevision(value);
                        break;
                }
            }
        }
        result.setModel(new Model(new Vendor(vendor), model));
        result.setRegion(new Region(region));
        return result;
    }

    private Map<Integer, Type> getHeaders(Row row) throws InvalidFormatException {
        Map<String, Integer> result = new HashMap<>();
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (cell.getCellType().equals(CellType.STRING))
                result.put(cell.getStringCellValue().toUpperCase().replaceAll("_", ""), cell.getColumnIndex());
        }
        if (!Arrays.stream(MANDATORY_HEADERS).allMatch(s -> result.keySet().stream().anyMatch(s::equalsIgnoreCase)))
            throw new InvalidFormatException("Заголовок файла некорректный");

        return result.entrySet().stream().filter(e -> Type.isExist(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getValue, e -> Type.getByValue(e.getKey())));
    }

    enum Type {
        HOST("SNMPADDRESS"),
        NAME("NAME"),
        VENDOR("VENDOR"),
        MODEL("MODEL"),
        REGION("MTSCUSTOM3"),
        LOCATION("MTSLOCATION"),
        FIRMREVISION("MTSFIRMREVISION"),
        FIRMWARE("MTSFIRMWARE");

        private String name;

        Type(String name) {
            this.name = name;
        }

        public static Type getByValue(String value) {
            return Arrays.stream(Type.values()).filter(t -> t.name.equalsIgnoreCase(value)).findFirst().orElse(null);
        }

        public static boolean isExist(String value) {
            return Optional.ofNullable(getByValue(value)).isPresent();
        }
    }
}
