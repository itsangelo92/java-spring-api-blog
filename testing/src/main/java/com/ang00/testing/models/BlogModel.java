package com.ang00.testing.models;

import java.sql.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "blog")
public class BlogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "banner_img")
    private String bannerImg;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "date")
    private Date date;

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

    public Date getDate() {
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

    public void setBannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAuthor(UserModel author) {
        this.author = author;
    }

}
