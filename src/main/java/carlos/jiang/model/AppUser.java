package carlos.jiang.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String username;

    @Column(nullable = false, unique = true, length = 80)
    private String account;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(length = 80)
    private String recipientName;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String address;

    private LocalDateTime createdAt;

    protected AppUser() {
    }

    public AppUser(String username, String account, String passwordHash) {
        this.username = username;
        this.account = account;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
    }

    public void updateProfile(String recipientName, String phone, String address) {
        this.recipientName = recipientName;
        this.phone = phone;
        this.address = address;
    }

    public boolean hasShippingProfile() {
        return hasText(recipientName) && hasText(phone) && hasText(address);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAccount() {
        return account;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
