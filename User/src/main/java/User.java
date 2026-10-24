public class User {
    private int id;
    private String name;
    private String password;

    public User(int id, String name, String password)  {
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.id =id;
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
        return this.password.equals(attempt);
    }

}


