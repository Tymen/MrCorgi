package dev.tymen.MrCorgi.repositories.moviedb;

import dev.tymen.MrCorgi.models.entities.moviedb.Movie;
import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<Movie, String> {
}
