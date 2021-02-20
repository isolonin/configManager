package i.solonin.configmanager.service.repos;

import i.solonin.configmanager.model.master.Model;
import i.solonin.configmanager.model.master.Vendor;
import i.solonin.configmanager.service.repos.custom.ModelRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModelRepository extends JpaRepository<Model, Long>, AbstractRepository<Model>, ModelRepositoryCustom {
    Model findByNameAndVendor(String name, Vendor vendor);

    List<Model> findAllByNameIgnoreCase(String name);
}
