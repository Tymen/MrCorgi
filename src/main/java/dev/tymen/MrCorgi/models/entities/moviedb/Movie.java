package dev.tymen.MrCorgi.models.entities.moviedb;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "movies")
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String movieId;
    private String imdbId;
    private String title;
    private String originalTitle;
    private int releaseYear;
    private Integer endYear;
    private String releaseDate;
    private boolean isSeries;
    private boolean isEpisode;

    @OneToMany(mappedBy = "movie")
    private List<Image> images;

    @ManyToOne
    @JoinColumn(name = "title_type_id")
    private TitleType titleType;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
