package com.example;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Post> posts = new ArrayList<>();

        for (int i = 1; i <= 1_000_000; i++) {
            int randomLikes = (int)(Math.random() * Math.random() * 101);
            String randomId = UUID.randomUUID().toString();
            posts.add(new Post(randomId, randomLikes));
        }

        printList(getTop10(posts));
    }

     static List<Post> getTop10(List<Post> posts){
        PriorityQueue<Post> postsQueue = new PriorityQueue<>(Comparator.comparing(Post::getLikesCount));

        for (Post post : posts) {
            if (postsQueue.size() < 10){
                postsQueue.offer(post);
            }
            else {
                if (post.getLikesCount() > postsQueue.peek().getLikesCount()){
                    postsQueue.poll();
                    postsQueue.offer(post);
                }
            }
        }
         return postsQueue.stream()
                 .sorted(Comparator.comparing(Post::getLikesCount).reversed())
                 .collect(Collectors.toList());
    }

    static void printList(List<Post> posts){
        for (int i = 1; i <= posts.size(); i++){
            System.out.println(i + ". " + posts.get(i-1).toString());
        }
    }
}