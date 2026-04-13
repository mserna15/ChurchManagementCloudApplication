package com.ccms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Body is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "posted_date", updatable = false)
    private LocalDateTime postedDate;

    // ── Constructors ──────────────────────────────────────

    public Announcement() {}

    public Announcement(String title, String body) {
        this.title = title;
        this.body = body;
    }

    // ── Lifecycle ─────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        this.postedDate = LocalDateTime.now();
    }

    // ── Getters & Setters ─────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public LocalDateTime getPostedDate() { return postedDate; }
    public void setPostedDate(LocalDateTime postedDate) { this.postedDate = postedDate; }

    @Override
    public String toString() {
        return "Announcement{id=" + id + ", title='" + title + "', postedDate=" + postedDate + '}';
    }
}