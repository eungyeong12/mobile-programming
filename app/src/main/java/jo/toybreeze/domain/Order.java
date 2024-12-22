package jo.toybreeze.domain;

import java.util.Date;

public class Order {
    private Date startDate;
    private Date endDate;
    private String toyId;
    private String image;
    private String name;
    private int price;
    private Date createdAt;

    public Order() {
    }

    public Order(Date startDate, Date endDate, String toyId, String image, String name, int price, Date createdAt) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.toyId = toyId;
        this.image = image;
        this.name = name;
        this.price = price;
        this.createdAt = createdAt;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getToyId() {
        return toyId;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public int getPrice() {
        return price;
    }
}
