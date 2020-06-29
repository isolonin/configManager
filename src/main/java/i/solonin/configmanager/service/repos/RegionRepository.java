package i.solonin.configmanager.service.repos;

import i.solonin.configmanager.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
    boolean existsByName(String name);
}
