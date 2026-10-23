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

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof User other)) return false;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode(){
        return this.name.hashCode();
    }

    public String getName(){
        return this.name;
    }

    public boolean checkPassword(String attempt) {
        return this.password.equals(attempt);
    }

}


