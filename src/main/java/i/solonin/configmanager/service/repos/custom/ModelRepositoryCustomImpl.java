package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.master.Model;
import i.solonin.configmanager.model.master.Vendor;
import i.solonin.configmanager.service.repos.ModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class ModelRepositoryCustomImpl implements ModelRepositoryCustom {
    private final ModelRepository modelRepository;

    @Override
    public Model getByNameAndVendorOrCreate(String name, Vendor vendor) {
        List<Model> models = modelRepository.findAllByNameIgnoreCase(name);
        Model result = models.stream().filter(m -> m.getVendor().equals(vendor)).findFirst().orElse(null);
        if (result == null) {
            result = new Model();
            result.setName(name);
            result.setVendor(vendor);
            modelRepository.save(result);
        }
        return result;
    }
}
