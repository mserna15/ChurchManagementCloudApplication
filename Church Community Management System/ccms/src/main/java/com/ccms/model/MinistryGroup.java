package com.ccms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "ministry_groups")
public class MinistryGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Group name is required")
    @Size(max = 150, message = "Group name must not exceed 150 characters")
    @Column(name = "group_name", nullable = false, unique = true, length = 150)
    private String groupName;

    @Column(columnDefinition = "TEXT")
    private String description;

    // One ministry group can have many members
    @OneToMany(mappedBy = "ministryGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Member> members;

    // ── Constructors ──────────────────────────────────────

    public MinistryGroup() {}

    public MinistryGroup(String groupName, String description) {
        this.groupName = groupName;
        this.description = description;
    }

    // ── Getters & Setters ─────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    // ── toString ──────────────────────────────────────────

    @Override
    public String toString() {
        return "MinistryGroup{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}