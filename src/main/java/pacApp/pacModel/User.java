package pacApp.pacModel;

import javax.persistence.*;

@Entity
@Table(name = "User")
public class User {

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID", updatable = false, nullable = false)
    private long id;
    private String email;
    private String password;
    private Currency defaultCurrency;

    protected User(){}

    public User(String email){
        this.email = email;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(long id, String email){
        this.id = id;
        this.email = email;
    }

    public User(long id, String email, String password){
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return this.id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String name) {
        this.email = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Currency getDefaultCurrency() {
        return this.defaultCurrency;
    }

    public void setDefaultCurrency(Currency currency) {
        this.defaultCurrency = currency;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, email='%s', password='%s']",id , email, password);
    }
}
