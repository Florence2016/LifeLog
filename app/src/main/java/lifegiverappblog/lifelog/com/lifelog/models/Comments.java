package lifegiverappblog.lifelog.com.lifelog.models;

import java.util.Date;

public class Comments {


    private String user_id;
    private String message;
    private Date timestamp;

    public Comments(){

    }

    public Comments(String user_id, String message, Date timestamp) {
        this.user_id = user_id;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}
