package jo.toybreeze.domain;

public class User {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private String detailAddress;

    public User(String name, String email, String password, String phoneNumber, String address, String detailAddress) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.detailAddress = detailAddress;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
