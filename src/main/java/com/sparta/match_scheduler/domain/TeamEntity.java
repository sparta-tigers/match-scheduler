package com.sparta.match_scheduler.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "teams")
public class TeamEntity extends BaseEntity {

    @Id
    @Column(name = "team_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false)
    @Enumerated(EnumType.STRING)
    private TeamCode teamCode;

    @Column(name = "name", nullable = false)
    private String name;
    
    // For JPA
    protected TeamEntity() {}

    public static TeamEntity of(TeamCode teamCode, String name) {
        TeamEntity team = new TeamEntity();
        team.teamCode = teamCode;
        team.name = name;

        return team;
    }

    // getters
    public Long getId() {
        return id;
    }

    public TeamCode getTeamCode() {
        return teamCode;
    }

    public String getName() {
        return name;
    }
    
}