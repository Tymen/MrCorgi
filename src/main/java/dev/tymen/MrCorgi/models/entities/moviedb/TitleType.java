package dev.tymen.MrCorgi.models.entities.moviedb;

import dev.tymen.MrCorgi.models.entities.moviedb.Episode;
import dev.tymen.MrCorgi.models.entities.moviedb.Movie;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
@Table(name = "title_types")
@NoArgsConstructor
public class TitleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long titleTypeId;

    private String typeText;
    private String typeId;
    private boolean canHaveEpisodes;

    @OneToMany(mappedBy = "titleType")
    private List<Movie> movies;

    @OneToMany(mappedBy = "titleType")
    private List<Episode> episodes;
}
