package gov.tech.mini.dinedecider.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class SessionMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", updatable = false)
    private Long userId;

    @Column(name = "session_id", updatable = false)
    private Long sessionId;

    @Column(name = "status")
    private MemberStatus status;


}
