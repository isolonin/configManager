package i.solonin.configmanager.service.repos;

import i.solonin.configmanager.model.Region;
import i.solonin.configmanager.service.repos.custom.RegionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long>, AbstractRepository<Region>, RegionRepositoryCustom {
}
