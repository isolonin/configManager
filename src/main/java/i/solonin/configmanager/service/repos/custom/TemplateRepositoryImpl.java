package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.Template;
import i.solonin.configmanager.service.repos.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class TemplateRepositoryImpl implements TemplateRepositoryCustom {
    private final TemplateRepository templateRepository;

    @Override
    public boolean existsByName(Template template) {
        Template oldTemplate = templateRepository.findByNameIgnoreCase(template.getName());
        if (template.isNew())
            return oldTemplate != null;
        else
            return oldTemplate != null && !oldTemplate.equals(template);
    }
}
