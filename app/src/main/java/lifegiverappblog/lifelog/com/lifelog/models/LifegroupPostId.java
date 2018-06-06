package lifegiverappblog.lifelog.com.lifelog.models;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class LifegroupPostId {

    @Exclude
    public String LifegroupPostId;

    public <T extends LifegroupPostId> T withId(@NonNull final String id) {
        this.LifegroupPostId = id;
        return (T) this;
    }

}
