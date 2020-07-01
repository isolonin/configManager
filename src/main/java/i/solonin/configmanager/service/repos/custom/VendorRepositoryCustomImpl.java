package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.Vendor;
import i.solonin.configmanager.service.repos.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
@Transactional
public class VendorRepositoryCustomImpl implements VendorRepositoryCustom {
    private final EntityManager entityManager;
    private final VendorRepository vendorRepository;

    @Override
    public Vendor getByNameOrCreate(String name) {
        Vendor result = vendorRepository.findByNameIgnoreCase(name);
        if (result == null) {
            result = new Vendor();
            result.setName(name);
            entityManager.persist(result);
        }
        return result;
    }
}
