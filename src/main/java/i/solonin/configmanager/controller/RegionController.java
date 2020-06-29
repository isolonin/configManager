package i.solonin.configmanager.controller;

import i.solonin.configmanager.model.Region;
import i.solonin.configmanager.service.repos.RegionRepository;
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
@Named("regionController")
@Slf4j
@ViewScoped
public class RegionController extends AbstractController {
    @Autowired
    private RegionRepository regionRepository;

    private Region region = new Region();
    @Setter
    private List<Region> filteredRegions;
    private List<Region> regions;

    public void init() {
        regions = regionRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public void add() {
        try {
            save(region);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            showErrorMessage(ex.getMessage());
            PrimeFaces.current().ajax().addCallbackParam("failed", true);
        }
    }

    private boolean save(Region region) {
        if (regionRepository.existsByName(region.getName())) {
            showWarnMessage("Регион с таким именем уже существует");
            PrimeFaces.current().ajax().addCallbackParam("failed", true);
            return false;
        }
        save(region, regions, filteredRegions, regionRepository);
        this.region = new Region();
        return true;
    }

    public void onCellEdit(CellEditEvent event) {
        Region region = regions.get(event.getRowIndex());
        if (!save(region)) region.setName(String.valueOf(event.getOldValue()));
    }

    public void remove(Region region) {
        remove(region, regions, filteredRegions, regionRepository);
    }
}
