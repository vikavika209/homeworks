package com.example;

public class Post {
    private String text;
    private Integer likesCount;

    public Post(String text, Integer likesCount) {

        this.text = text;
        this.likesCount = likesCount;
    }

    public String getText() {
        return text;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    @Override
    public String toString() {
        return "Post{" +
                "text='" + text + '\'' +
                ", likesCount=" + likesCount +
                '}';
    }
}
