package i.solonin.configmanager.controller;

import i.solonin.configmanager.model.EquipmentType;
import i.solonin.configmanager.model.Model;
import i.solonin.configmanager.model.Template;
import i.solonin.configmanager.model.Vendor;
import i.solonin.configmanager.service.repos.ModelRepository;
import i.solonin.configmanager.service.repos.TemplateRepository;
import i.solonin.configmanager.service.repos.VendorRepository;
import i.solonin.configmanager.service.repos.custom.AbstractRepositoryCustom;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

@Getter
@Named("templateController")
@Slf4j
@ViewScoped
public class TemplateController extends AbstractController {
    @Qualifier("templateRepositoryImpl")
    @Autowired
    private AbstractRepositoryCustom templateRepositoryCustom;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private ModelRepository modelRepository;

    private Template template = new Template();
    @Setter
    private List<Template> filteredTemplates;
    private List<Template> templates;
    private List<Vendor> vendors;
    private List<Model> models;
    private final List<EquipmentType> equipmentTypes = Arrays.asList(EquipmentType.class.getEnumConstants());

    public void init() {
        templates = templateRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        vendors = vendorRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        models = modelRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public void updateFile(FileUploadEvent event) {
        Template updatedTemplate = (Template) event.getComponent().getAttributes().get("template");
        UploadedFile file = event.getFile();
        setFile(file, updatedTemplate);
        templateRepository.save(updatedTemplate);
    }

    public void uploadFile(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        setFile(file, template);
    }

    public void setFile(UploadedFile file, Template t) {
        t.setFile(file);
        t.setFileName(file.getFileName());
        t.setContentType(file.getContentType());
        t.setData(file.getContent());
    }

    public void save() {
        try {
            save(template);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            showErrorMessage(ex.getMessage());
            PrimeFaces.current().ajax().addCallbackParam("failed", true);
        }
    }

    private void save(Template template) {
        if (!validate(template)) return;

        save(template, templates, filteredTemplates, templateRepository);
        this.template = new Template();
    }

    private boolean validate(Template template) {
        if (template.getFile() == null) {
            showWarnMessage("Необходимо загрузить файл шаблона");
            PrimeFaces.current().ajax().addCallbackParam("failed", true);
            return false;
        }
        if (templateRepositoryCustom.existsByName(template)) {
            showWarnMessage("Шаблон с таким именем уже существует");
            PrimeFaces.current().ajax().addCallbackParam("failed", true);
            return false;
        }
        return true;
    }

    public void onCellEdit(CellEditEvent event) {
        Template template = templates.get(event.getRowIndex());
        if (validate(template))
            templateRepository.save(template);
        else
            template.setName(String.valueOf(event.getOldValue()));
    }

    public void remove(Template template) {
        template.getModels().forEach(m -> {
            m.setTemplate(null);
            modelRepository.save(m);
        });
        template.getModels().clear();
        remove(template, templates, filteredTemplates, templateRepository);
    }
}
