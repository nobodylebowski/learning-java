import org.mindrot.jbcrypt.BCrypt;

public class User {
    private int id;
    private String name;
    private String passwordHash;

    public User(int id, String name, String passwordHash)  {
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.id =id;
        this.name = name;
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "User: " + this.name;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof User other)) return false;
        return this.id == other.id;
    }

    @Override
    public int hashCode(){
        return this.id;
    }

    public int getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }

    public boolean checkPassword(String attempt) {
        return BCrypt.checkpw(attempt, this.passwordHash);
    }

}


