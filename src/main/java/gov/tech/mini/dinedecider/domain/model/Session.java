package gov.tech.mini.dinedecider.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_id", updatable = false)
    private Long adminId;

    @Column(name = "start_datetime", updatable = false)
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime")
    private LocalDateTime endDatetime;
}
