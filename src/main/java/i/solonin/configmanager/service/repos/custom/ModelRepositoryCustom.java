package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.master.Model;
import i.solonin.configmanager.model.master.Vendor;

public interface ModelRepositoryCustom {
    Model getByNameAndVendorOrCreate(String name, Vendor vendor);
}
