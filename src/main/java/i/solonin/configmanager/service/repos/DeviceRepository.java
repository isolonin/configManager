package i.solonin.configmanager.service.repos;

import i.solonin.configmanager.model.master.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Device findByHost(String host);
}