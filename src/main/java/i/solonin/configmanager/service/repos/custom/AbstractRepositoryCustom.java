package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.DBId;
import i.solonin.configmanager.model.WithName;

public interface AbstractRepositoryCustom {
    <T extends DBId & WithName> boolean existsByName(T t);

    default <T extends DBId & WithName> boolean isExist(T newEntry, T oldEntry){
        if (newEntry.isNew())
            return oldEntry != null;
        else
            return oldEntry != null && !oldEntry.equals(newEntry);
    }
}
