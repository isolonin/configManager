package i.solonin.configmanager.service.repos;

import i.solonin.configmanager.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    boolean existsByName(String name);
}
