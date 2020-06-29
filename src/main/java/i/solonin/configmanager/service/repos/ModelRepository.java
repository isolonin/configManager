package i.solonin.configmanager.service.repos;

import i.solonin.configmanager.model.Model;
import i.solonin.configmanager.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelRepository extends JpaRepository<Model, Long> {
    boolean existsByNameAndVendor(String name, Vendor vendor);
}
