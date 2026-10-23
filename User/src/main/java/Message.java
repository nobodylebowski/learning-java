import java.time.LocalDateTime;

public class Message {
    private User sender;
    private User receiver;
    private String content;
    private LocalDateTime sentAt;

    public Message(User sender, User receiver, String content){
        if(sender == null || receiver == null){
            throw new IllegalArgumentException("Sender and receiver are required");
        }
        if(content == null || content.isBlank()){
            throw new IllegalArgumentException("Content cannot be empty");
        }
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sentAt = LocalDateTime.now();
    }

    public User getSender(){
        return this.sender;
    }

    public User getReceiver(){
        return this.receiver;
    }

    public String getContent(){
        return this.content;
    }

    public LocalDateTime getSentAt(){
        return this.sentAt;
    }

    @Override
    public String toString(){
        return "[" + this.sentAt + "] " + sender.getName() + "->" + receiver.getName() + " : " + this.content;
    }
}

