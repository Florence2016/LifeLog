package lifegiverappblog.lifelog.com.lifelog.models;

import java.util.Date;

public class LifegroupPost_model extends LifegroupPostId{

    private String user_id;
    private String name;
    private String attendee;
    private String image_url;
    private String image_thumb;

    private Date timestamp;

    public LifegroupPost_model(){}

    public LifegroupPost_model(String user_id,String name, String attendee, String image_url, String image_thumb, Date timestamp) {
        this.user_id = user_id;
        this.name = name;
        this.attendee = attendee;
        this.image_url = image_url;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttendee() {
        return attendee;
    }

    public void setAttendee(String attendee) {
        this.attendee = attendee;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
