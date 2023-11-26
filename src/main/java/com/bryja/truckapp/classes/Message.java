package com.bryja.truckapp.classes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String content;
    public LocalDateTime timestamp;

    public String chatroom;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity=User.class)
    @JsonBackReference
    @JsonIgnore
    private User user;

    @Nullable
    private String author_name;

    @Nullable
    private byte[] avr;

    @Transient
    private Long userid;
    public Message() {
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public byte[] getAvatar() {
        return avr;
    }

    public byte[] getAvr() {
        return avr;
    }

    public void setAvr(byte[] avr) {
        this.avr = avr;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public void setAvatar(byte[] avatar) {
        this.avr = avatar;
    }

    public Message(Long id, String content, LocalDateTime timestamp, User user) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public Message(Long id, String content, LocalDateTime timestamp, String chatroom, User user) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.chatroom = chatroom;
        this.user = user;
    }


    public String getChatroom() {
        return chatroom;
    }

    public void setChatroom(String chatroom) {
        this.chatroom = chatroom;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
