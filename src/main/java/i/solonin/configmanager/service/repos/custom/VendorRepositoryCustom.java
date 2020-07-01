package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.Vendor;

public interface VendorRepositoryCustom {
    Vendor getByNameOrCreate(String name);
}
