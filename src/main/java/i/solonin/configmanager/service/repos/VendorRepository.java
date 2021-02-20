package i.solonin.configmanager.service.repos;

import i.solonin.configmanager.model.master.Vendor;
import i.solonin.configmanager.service.repos.custom.VendorRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Long>, AbstractRepository<Vendor>, VendorRepositoryCustom {
}
