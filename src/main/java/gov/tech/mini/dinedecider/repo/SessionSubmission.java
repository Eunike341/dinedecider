package gov.tech.mini.dinedecider.repo;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class SessionSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", updatable = false)
    private Long sessionId;

    @Column(name = "placeName", updatable = false)
    private String placeName;

    @Column(name = "create_datetime", updatable = false)
    private LocalDateTime createDatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public LocalDateTime getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(LocalDateTime createDatetime) {
        this.createDatetime = createDatetime;
    }
}
