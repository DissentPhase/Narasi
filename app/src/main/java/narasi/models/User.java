package narasi.models;

public abstract class User {
    protected int id; // added field id
    protected String username;
    protected String password;
    protected String fullName;
    protected String email;
    protected String anonymousId; 

    public User(String username, String password, String fullName, String email, String anonymousId) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.anonymousId = anonymousId; 
    }

    public abstract void publishWork(Work work);

    public abstract void manageWork(Work work);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAnonymousId() {
        return anonymousId;
    }

    public void setAnonymousId(String anonymousId) {
        this.anonymousId = anonymousId;
    }
}
