package com.ang00.testing.models;

import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "blog")
public class BlogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "banner_img", columnDefinition = "varchar(255) default 'banners/default.png'")
    private String bannerImg = "banners/default.png";

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "date", columnDefinition = "Timestamp default CURRENT_TIMESTAMP()")
    private Timestamp date;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private UserModel author;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBannerImg() {
        return bannerImg;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getDate() {
        return date;
    }

    public UserModel getAuthor() {
        return author;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setBannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setAuthor(UserModel author) {
        this.author = author;
    }

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = new Timestamp(System.currentTimeMillis());
        }
    }

}
