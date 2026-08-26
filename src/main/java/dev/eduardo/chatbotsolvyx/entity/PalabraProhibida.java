package dev.eduardo.chatbotsolvyx.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "palabra_prohibida")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PalabraProhibida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "palabra", nullable = false, unique = true)
    private String palabra;

    @Column(name = "activo", nullable = false)
    private boolean activo;
}
