package com.cosmo.cats.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "api_keys")
@Getter
@Setter
public class ApiKeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "api_key_seq")
    @SequenceGenerator(name = "api_key_seq", sequenceName = "api_key_seq", allocationSize = 1)
    private Long id;

    @Column(name = "api_key", nullable = false, unique = true)
    private String apiKey;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "api_key_roles",
        joinColumns = @JoinColumn(name = "api_key_id")
    )
    @Column(name = "role")
    private Set<String> roles;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isValid() {
        return isActive && (expiresAt == null || expiresAt.isAfter(LocalDateTime.now()));
    }
}
