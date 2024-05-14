package gov.tech.mini.dinedecider.repo;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_user_id", nullable = false)
    private SessionUser sessionUser;

    @Column(name = "place_name", updatable = false)
    private String placeName;

    @Column(name = "selected")
    private boolean selected;

    @Column(name = "create_datetime", updatable = false)
    private LocalDateTime createDatetime;

    public Submission() {
    }

    public Submission(SessionUser sessionUser, String placeName, LocalDateTime createDatetime) {
        this.sessionUser = sessionUser;
        this.placeName = placeName;
        this.createDatetime = createDatetime;
    }

    public Submission(String placeName) {
        this.placeName = placeName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SessionUser getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(SessionUser sessionUser) {
        this.sessionUser = sessionUser;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public LocalDateTime getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(LocalDateTime createDatetime) {
        this.createDatetime = createDatetime;
    }
}
