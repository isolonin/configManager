package i.solonin.configmanager.controller;

import i.solonin.configmanager.model.Model;
import i.solonin.configmanager.model.Vendor;
import i.solonin.configmanager.service.repos.ModelRepository;
import i.solonin.configmanager.service.repos.VendorRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.List;

@Getter
@Named("modelController")
@Slf4j
@ViewScoped
public class ModelController extends AbstractController {
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private VendorRepository vendorRepository;

    private Model model = new Model();
    private List<Model> models;
    @Setter
    private List<Model> filteredModels;
    private List<Vendor> vendors;

    public void init() {
        models = modelRepository.findAll(Sort.by(Sort.Direction.ASC, "vendor.name", "name"));
        vendors = vendorRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public void add() {
        try {
            save(model);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            showErrorMessage(ex.getMessage());
            PrimeFaces.current().ajax().addCallbackParam("failed", true);
        }
    }

    private boolean save(Model model) {
        if (modelRepository.existsByNameAndVendor(model.getName(), model.getVendor())) {
            showWarnMessage("Модель с таким наименованием уже существует");
            PrimeFaces.current().ajax().addCallbackParam("failed", true);
            return false;
        }
        save(model, models, filteredModels, modelRepository);
        this.model = new Model();
        return true;
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Model model = models.get(event.getRowIndex());
        if (!save(model)) {
            if (oldValue instanceof Vendor)
                model.setVendor((Vendor) oldValue);
            if (oldValue instanceof String)
                model.setName(String.valueOf(oldValue));
        }
    }

    public void remove(Model model) {
        remove(model, models, filteredModels, modelRepository);
    }
}