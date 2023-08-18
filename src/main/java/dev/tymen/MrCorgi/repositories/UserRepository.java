package dev.tymen.MrCorgi.repositories;

import dev.tymen.MrCorgi.models.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
}
