package jo.toybreeze.domain;

import java.util.Date;

public class Review {
    private String reviewId;
    private String user;
    private String toy;
    private float rating;
    private String content;
    private Date createdAt;

    public Review() {
    }

    public Review(String reviewId, String user, String toy, float rating, String content, Date createdAt) {
        this.reviewId = reviewId;
        this.user = user;
        this.toy = toy;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getUser() {
        return user;
    }

    public String getToy() {
        return toy;
    }

    public String getContent() {
        return content;
    }

    public float getRating() {
        return rating;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}

