public class Main {
    public static void main(String[] args){
        User tom = new User("Tom", "psw1");
        User sara = new User("Sara", "psw2");
        Message msg1 = new Message(tom ,sara, "Hello Sara");

        System.out.println(msg1.toString());

        //Message msg2 = new Message(tom, null, "See you");
        Message msg3 = new Message(sara, tom, "");



    }
}
