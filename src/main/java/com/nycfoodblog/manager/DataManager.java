package com.nycfoodblog.manager;

import java.io.FileNotFoundException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
public class DataManager implements Managed {

    private final static String POSTS_DIR = "posts";
    private final static String REVIEWS_DIR = "reviews";
    private Path dataRootPath;
    private Path postsPath;
    private Map<String, Path> userReviewsPaths;

    private List<User> users;
    private ConcurrentMap<Long, Post> postMap;
    private ConcurrentMap<Long, List<Review>> reviewMap;
    private long maxId;

    public DataManager(Path dataRootPath, List<User> users) {
        this.dataRootPath = dataRootPath;
        this.users = users;
        userReviewsPaths = new HashMap<String, Path>();
        postMap = new ConcurrentHashMap<Long, Post>();
        reviewMap = new ConcurrentHashMap<Long, List<Review>>();
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
        Path reviewsPath = Paths.get(dataRootPath.toString(), REVIEWS_DIR);
        if (!Files.exists(reviewsPath)) {
            Files.createDirectory(reviewsPath);
        }
        for (User user : users) {
            Path userReviewsPath = Paths.get(reviewsPath.toString(), user.getUsername());
            if (!Files.exists(userReviewsPath)) {
                Files.createDirectory(userReviewsPath);
            }
            userReviewsPaths.put(user.getUsername(), userReviewsPath);
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
            DirectoryStream<Path> reviewsStream = Files.newDirectoryStream(userReviewsPaths.get(user), "*.json");
            for (Path entry : reviewsStream) {
                Review review = mapper.readValue(entry.toFile(), Review.class);
                if (!reviewMap.containsKey(review.getPostId())) {
                    reviewMap.put(review.getPostId(), new ArrayList<Review>());
                }
                reviewMap.get(review.getPostId()).add(review);
            }
        }

    }

    @Override
    public void stop() { }

    public Map<Long, Post> getAllPosts() { return postMap; }

    public synchronized long putPost(Post post) throws Exception {
        long id = post.getId() > 0 ? post.getId() : this.maxId + 1;
        post.setId(id);
        post.setDateCreated(new Date());
        this.maxId = Math.max(this.maxId, id);
        postMap.put(id, post);
        ObjectMapper mapper = new ObjectMapper();
        Path newPostPath = Paths.get(postsPath.toString(), id + ".json");
        mapper.writeValue(newPostPath.toFile(), post);
        return id;
    }

    public Map<Long, List<Review>> getAllReviews() { return reviewMap; }

    public synchronized void putReview(Review review) throws Exception {
        List<Review> postReviews = reviewMap.get(review.getPostId());
        List<Review> newPostReviews = new ArrayList<Review>();
        if (postReviews != null) {
            for (Review oldReview : postReviews) {
                if (!oldReview.getUsername().equals(review.getUsername())) {
                    newPostReviews.add(oldReview);
                }
            }
        }
        newPostReviews.add(review);
        reviewMap.put(review.getPostId(), newPostReviews);
        ObjectMapper mapper = new ObjectMapper();
        Path newReviewPath = Paths.get(userReviewsPaths.get(review.getUsername()).toString(), review.getPostId() + ".json");
        mapper.writeValue(newReviewPath.toFile(), review);
    }

    public List<String> getAllUsernames() {
        return this.users.stream().map(u -> u.getUsername() ).collect(Collectors.toList());
    }

}
