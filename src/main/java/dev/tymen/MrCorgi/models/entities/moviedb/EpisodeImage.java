package dev.tymen.MrCorgi.models.entities.moviedb;

import dev.tymen.MrCorgi.models.entities.moviedb.Episode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "episode_images")
@NoArgsConstructor
public class EpisodeImage {

    @Id
    private String imageId;

    @ManyToOne
    @JoinColumn(name = "episode_id")
    private Episode episode;

    private int width;
    private int height;
    private String url;
    private String caption;
}

