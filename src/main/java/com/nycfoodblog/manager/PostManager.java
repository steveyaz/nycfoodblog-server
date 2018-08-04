package com.nycfoodblog.manager;

import java.io.FileNotFoundException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nycfoodblog.api.Post;
import com.nycfoodblog.api.Review;
import com.nycfoodblog.auth.User;

import io.dropwizard.lifecycle.Managed;

@Singleton
public class PostManager implements Managed {

    private final static String POSTS_DIR = "posts";
    private final static String MEDIA_DIR = "media";
    private final static String REVIEWS_DIR = "reviews";
    private Path dataRootPath;
    private Path postsPath;
    private Path mediaPath;
    private Map<String, Path> userReviewsPaths;

    private List<User> userNames;
    private ConcurrentMap<Long, Post> postMap;
    private Map<String, ConcurrentMap<Long, Review>> userReviewMap;
    private long maxId;

    public PostManager(Path dataRootPath, List<User> users) {
        this.dataRootPath = dataRootPath;
        this.userNames = users;
        postMap = new ConcurrentHashMap<Long, Post>();
        userReviewsPaths = new HashMap<String, Path>();
        userReviewMap = new HashMap<String, ConcurrentMap<Long, Review>>();
        maxId = 0;
    }

    @Override
    public void start() throws Exception {

        // ensure data directory structure
        if (!Files.exists(dataRootPath)) {
            throw new FileNotFoundException("dataRootPath is not configured to a valid directory: " + dataRootPath.toString());
        }
        postsPath = Paths.get(dataRootPath.toString(), POSTS_DIR);
        if (!Files.exists(postsPath)) {
            Files.createDirectory(postsPath);
        }
        Path mediaPath = Paths.get(dataRootPath.toString(), MEDIA_DIR);
        if (!Files.exists(mediaPath)) {
            Files.createDirectory(mediaPath);
        }
        Path reviewsPath = Paths.get(dataRootPath.toString(), REVIEWS_DIR);
        if (!Files.exists(reviewsPath)) {
            Files.createDirectory(reviewsPath);
        }
        for (User user : userNames) {
            Path userReviewsPath = Paths.get(reviewsPath.toString(), user.getName().toLowerCase());
            if (!Files.exists(userReviewsPath)) {
                Files.createDirectory(userReviewsPath);
            }
            userReviewsPaths.put(user.getName().toLowerCase(), userReviewsPath);
        }

        // load posts from disk
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, true);
        DirectoryStream<Path> postsStream = Files.newDirectoryStream(postsPath, "*.json");
        for (Path entry: postsStream){
            Post post = mapper.readValue(entry.toFile(), Post.class);
            if (postMap.containsKey(post.getId())) {
                throw new Exception("Duplicate post id: " + post.getId());
            }
            this.maxId = Math.max(this.maxId, post.getId());
            postMap.put(post.getId(), post);
        }

        // load reviews from disk
        for (String user : userReviewsPaths.keySet()) {
            userReviewMap.put(user, new ConcurrentHashMap<Long, Review>());
            DirectoryStream<Path> reviewsStream = Files.newDirectoryStream(userReviewsPaths.get(user), "*.json");
            for (Path entry: reviewsStream){
                Review review = mapper.readValue(entry.toFile(), Review.class);
                if (userReviewMap.get(user).containsKey(review.getPostId())) {
                    throw new Exception("Duplicate review id: " + review.getPostId());
                }
                userReviewMap.get(user).put(review.getPostId(), review);
            }
        }

    }

    @Override
    public void stop() throws Exception {

    }

    public Long[] getAllPostIds() {
        Long[] ids = new Long[postMap.size()];
        postMap.keySet().toArray(ids);
        return ids;
    }

    public Post getPost(long id) {
        return postMap.getOrDefault(id, null);
    }

    public synchronized long putPost(Post post) throws Exception {
        long id = this.maxId + 1;
        post.setId(id);
        post.setDateCreated(new Date());
        this.maxId = id;
        postMap.put(id, post);
        ObjectMapper mapper = new ObjectMapper();
        Path newPostPath = Paths.get(postsPath.toString(), id + ".json");
        mapper.writeValue(newPostPath.toFile(), post);
        return id;
    }

    public Review getReview(String user, long id) {
        if (!userReviewMap.containsKey(user)) { return null; }
        return userReviewMap.get(user).getOrDefault(id, null);
    }

    public synchronized void putReview(Review review) throws Exception {
        String usernameKey = review.getUsername().toLowerCase();
        userReviewMap.get(usernameKey).put(review.getPostId(), review);
        ObjectMapper mapper = new ObjectMapper();
        Path newReviewPath = Paths.get(userReviewsPaths.get(usernameKey).toString(), review.getPostId() + ".json");
        mapper.writeValue(newReviewPath.toFile(), review);
    }

    public List<String> getAllUsernames() {
        return this.userNames.stream().map(u -> u.getName() ).collect(Collectors.toList());
    }

}
