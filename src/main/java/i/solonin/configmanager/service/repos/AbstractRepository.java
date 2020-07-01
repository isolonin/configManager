package i.solonin.configmanager.service.repos;

public interface AbstractRepository<T> {
    boolean existsByName(String name);

    T findByNameIgnoreCase(String name);
}
