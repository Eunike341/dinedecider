package gov.tech.mini.dinedecider.repo;

import jakarta.persistence.*;

@Entity
public class SessionInvitee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User attendee;

    @Column(name = "session_id", updatable = false)
    private Long sessionId;

    @Column(name = "status")
    private MemberStatus status;

    public SessionInvitee() {
    }

    public SessionInvitee(User attendee, Long sessionId, MemberStatus status) {
        this.attendee = attendee;
        this.sessionId = sessionId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getAttendee() {
        return attendee;
    }

    public void setAttendee(User attendee) {
        this.attendee = attendee;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }
}
