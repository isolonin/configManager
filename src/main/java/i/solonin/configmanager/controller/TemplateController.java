package i.solonin.configmanager.controller;

import i.solonin.configmanager.model.Model;
import i.solonin.configmanager.model.ShellCommand;
import i.solonin.configmanager.model.Template;
import i.solonin.configmanager.model.Vendor;
import i.solonin.configmanager.service.repos.ModelRepository;
import i.solonin.configmanager.service.repos.ShellCommandRepository;
import i.solonin.configmanager.service.repos.TemplateRepository;
import i.solonin.configmanager.service.repos.VendorRepository;
import i.solonin.configmanager.service.repos.custom.AbstractRepositoryCustom;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
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
    private ShellCommandRepository shellCommandRepository;
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
    private List<ShellCommand> shellCommands;

    public void init() {
        templates = templateRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        vendors = vendorRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        models = modelRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        shellCommands = shellCommandRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public void uploadFile(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        template.setFile(file);
        template.setFileName(file.getFileName());
        template.setContentType(file.getContentType());
        template.setData(file.getContents());
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

    private boolean save(Template template) {
        if (templateRepositoryCustom.existsByName(template)) {
            showWarnMessage("Шаблон с таким именем уже существует");
            PrimeFaces.current().ajax().addCallbackParam("failed", true);
            return false;
        }
        save(template, templates, filteredTemplates, templateRepository);
        this.template = new Template();
        return true;
    }

    public void onCellEdit(CellEditEvent event) {
        Template template = templates.get(event.getRowIndex());
        if (!save(template)) template.setName(String.valueOf(event.getOldValue()));
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
