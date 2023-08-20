package dev.tymen.MrCorgi.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(nullable = false, unique = true, updatable = false)
    private String id;
    @Column(nullable = false)
    private String name;
}
