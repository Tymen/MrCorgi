package dev.tymen.MrCorgi.models.entities.moviedb;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "images")
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String imageId;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    private int width;
    private int height;
    private String url;
    private String caption;
}
