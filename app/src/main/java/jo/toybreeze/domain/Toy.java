package jo.toybreeze.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Toy {
    private String company;
    private String name;
    private String age;
    private String category;
    private List<String> tags;
    private int monthPrice;
    private int threeMonthPrice;
    private int quantity;
    private int sellQuantity;
    private String description;
    private String policy;

    public Toy() {
    }

    public Toy(String company, String name, String age, String category, List<String> tags, int monthPrice, int threeMonthPrice, int quantity, int sellQuantity, String description, String policy) {
        this.company = company;
        this.name = name;
        this.age = age;
        this.category = category;
        this.tags = new ArrayList<>(tags);
        this.monthPrice = monthPrice;
        this.threeMonthPrice = threeMonthPrice;
        this.quantity = quantity;
        this.sellQuantity = sellQuantity;
        this.description = description;
        this.policy = policy;
    }

    public String getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getAge() {
        return age;
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public int getMonthPrice() {
        return monthPrice;
    }

    public int getThreeMonthPrice() {
        return threeMonthPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getSellQuantity() {
        return sellQuantity;
    }

    public String getDescription() {
        return description;
    }

    public String getPolicy() {
        return policy;
    }
}
