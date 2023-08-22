package dev.tymen.MrCorgi.models.entities.moviedb;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "categories")
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String value;

    @OneToMany(mappedBy = "category")
    private List<Movie> movies;

    @OneToMany(mappedBy = "category")
    private List<Episode> episodes;
}
