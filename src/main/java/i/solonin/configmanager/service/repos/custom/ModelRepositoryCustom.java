package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.Model;
import i.solonin.configmanager.model.Vendor;

public interface ModelRepositoryCustom {
    Model getByNameAndVendorOrCreate(String name, Vendor vendor);
}
