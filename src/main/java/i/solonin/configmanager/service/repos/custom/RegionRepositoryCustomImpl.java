package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.master.Region;
import i.solonin.configmanager.service.repos.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
@Transactional
public class RegionRepositoryCustomImpl implements RegionRepositoryCustom {
    private final EntityManager entityManager;
    private final RegionRepository regionRepository;

    @Override
    public Region getByNameOrCreate(String name) {
        Region result = regionRepository.findByNameIgnoreCase(name);
        if (result == null) {
            result = new Region();
            result.setName(name);
            entityManager.persist(result);
        }
        return result;
    }
}
