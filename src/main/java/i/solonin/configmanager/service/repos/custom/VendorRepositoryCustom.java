package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.master.Vendor;

public interface VendorRepositoryCustom {
    Vendor getByNameOrCreate(String name);
}
