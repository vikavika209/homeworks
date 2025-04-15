package com.example;

import java.util.*;
import java.util.stream.Collectors;

public class SearchServiceImpl implements SearchService {

    @Override
    public List<User> searchForFriendsInWidth(User me, String name) {
        Queue<User> queue = new LinkedList<>();
        Set<User> visited = new HashSet<>();
        List<User> result = new ArrayList<>();

        queue.add(me);
        visited.add(me);

        while (!queue.isEmpty()) {
            User user = queue.poll();

            if (user.getName().equals(name) && !user.equals(me)) {
                result.add(user);
            }

            for (User friend : user.getFriends()) {
                if (!visited.contains(friend)) {
                    queue.add(friend);
                    visited.add(friend);
                }
            }
        }
        return result;
    }

    @Override
    public List<User> searchForFriendsInDepth(User me, String name) {
        Set<User> visited = new HashSet<>();
        List<User> result = new ArrayList<>();
        dfs(me, name, visited, result);
        return result;
    }

    private void dfs(User current, String name, Set<User> visited, List<User> result) {
        if (visited.contains(current)) return;
        visited.add(current);

        for (User friend : current.getFriends()) {
            if (friend.getName().equals(name) && !friend.equals(current)) {
                result.add(friend);
            }

            dfs(friend, name, visited, result);
        }
    }
}
