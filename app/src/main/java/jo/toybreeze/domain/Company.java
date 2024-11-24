package jo.toybreeze.domain;

public class Company {
    private String name;
    private Image logo;

    public Company(String name, Image logo) {
        this.name = name;
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public Image getLogo() {
        return logo;
    }
}
