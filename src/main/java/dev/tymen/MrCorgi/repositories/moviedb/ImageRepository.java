package dev.tymen.MrCorgi.repositories.moviedb;

import dev.tymen.MrCorgi.models.entities.moviedb.Image;
import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image, String> {
}
