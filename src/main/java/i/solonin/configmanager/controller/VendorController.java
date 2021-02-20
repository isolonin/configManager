package i.solonin.configmanager.controller;

import i.solonin.configmanager.model.master.Vendor;
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
@Named("vendorController")
@Slf4j
@ViewScoped
public class VendorController extends AbstractController {
    @Autowired
    private VendorRepository vendorRepository;

    private Vendor vendor = new Vendor();
    @Setter
    private List<Vendor> filteredVendors;
    private List<Vendor> vendors;

    public void init() {
        vendors = vendorRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public void save() {
        try {
            save(vendor);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            showErrorMessage(ex.getMessage());
            PrimeFaces.current().ajax().addCallbackParam("failed", true);
        }
    }

    private boolean save(Vendor vendor) {
        if (vendorRepository.existsByName(vendor.getName())) {
            showWarnMessage("Производитель с таким именем уже существует");
            PrimeFaces.current().ajax().addCallbackParam("failed", true);
            return false;
        }
        save(vendor, vendors, filteredVendors, vendorRepository);
        this.vendor = new Vendor();
        return true;
    }

    public void onCellEdit(CellEditEvent event) {
        Vendor vendor = vendors.get(event.getRowIndex());
        if (!save(vendor)) vendor.setName(String.valueOf(event.getOldValue()));
    }

    public void remove(Vendor vendor) {
        remove(vendor, vendors, filteredVendors, vendorRepository);
    }
}
