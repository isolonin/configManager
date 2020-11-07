package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.DBEntity;
import i.solonin.configmanager.model.Template;
import i.solonin.configmanager.model.WithName;
import i.solonin.configmanager.service.repos.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class TemplateRepositoryImpl implements AbstractRepositoryCustom {
    private final TemplateRepository templateRepository;

    @Override
    public <T extends DBEntity & WithName> boolean existsByName(T t) {
        Template oldTemplate = templateRepository.findByNameIgnoreCase(t.getName());
        return isExist(t, oldTemplate);
    }
}
