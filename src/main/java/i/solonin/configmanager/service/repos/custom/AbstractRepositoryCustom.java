package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.DBEntity;
import i.solonin.configmanager.model.WithName;

public interface AbstractRepositoryCustom {
    <T extends DBEntity & WithName> boolean existsByName(T t);

    default <T extends DBEntity & WithName> boolean isExist(T newEntry, T oldEntry){
        if (newEntry.isNew())
            return oldEntry != null;
        else
            return oldEntry != null && !oldEntry.equals(newEntry);
    }
}
