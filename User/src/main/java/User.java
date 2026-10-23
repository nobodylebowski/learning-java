public class User {
    private String name;
    private String password;

    public User(String name, String password)  {
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User: " + this.name;
    }

    public String getName(){
        return this.name;
    }

    public boolean checkPassword(String attempt) {
        return this.password.equals(attempt);
    }

}


