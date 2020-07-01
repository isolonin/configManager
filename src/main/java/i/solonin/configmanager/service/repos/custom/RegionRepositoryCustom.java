package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.Region;

public interface RegionRepositoryCustom {
    Region getByNameOrCreate(String name);
}
