package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.DBEntity;
import i.solonin.configmanager.model.ShellCommand;
import i.solonin.configmanager.model.WithName;
import i.solonin.configmanager.service.repos.ShellCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class ShellCommandRepositoryCustomImpl implements AbstractRepositoryCustom {
    private final ShellCommandRepository shellCommandRepository;

    @Override
    public <T extends DBEntity & WithName> boolean existsByName(T t) {
        ShellCommand oldShellCommand = shellCommandRepository.findByNameIgnoreCase(t.getName());
        return isExist(t, oldShellCommand);
    }
}
