package jo.toybreeze.domain;

import java.io.Serializable;
import java.util.Date;

public class PaymentToy implements Serializable {
    private String image;
    private String name;
    private int quantity;
    private int maxQuantity;
    private int price;
    private String paymentType;
    private Date createdAt;

    public PaymentToy() {

    }

    public PaymentToy(String image, String name, int quantity, int maxQuantity, int price, String paymentType, Date createdAt) {
        this.image = image;
        this.name = name;
        this.quantity = quantity;
        this.maxQuantity = maxQuantity;
        this.price = price;
        this.paymentType = paymentType;
        this.createdAt = createdAt;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public int getPrice() {
        return price;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
