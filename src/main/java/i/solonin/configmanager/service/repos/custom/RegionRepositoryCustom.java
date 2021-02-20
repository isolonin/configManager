package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.master.Region;

public interface RegionRepositoryCustom {
    Region getByNameOrCreate(String name);
}
