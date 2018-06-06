package lifegiverappblog.lifelog.com.lifelog.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import lifegiverappblog.lifelog.com.lifelog.HomeMainActivity;
import lifegiverappblog.lifelog.com.lifelog.R;
import lifegiverappblog.lifelog.com.lifelog.comments.CommentsActivity;
import lifegiverappblog.lifelog.com.lifelog.models.LifegroupPost_model;

public class LifegroupRecyclerAdapter extends RecyclerView.Adapter<LifegroupRecyclerAdapter.ViewHolder>{

    public List<LifegroupPost_model>lifegroup_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public LifegroupRecyclerAdapter(List<LifegroupPost_model> lifegroup_list){

        this.lifegroup_list = lifegroup_list;
    }

    //this three methods requires for this adapter
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lifegroup_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.setIsRecyclable(false);

        final String lgPostId = lifegroup_list.get(position).LifegroupPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        String attendees_data,lifegroup_data, lifegroup_image_url, lifegroup_thumbUri;

        attendees_data = lifegroup_list.get(position).getAttendee();
        holder.setAttendeeText(attendees_data);

        lifegroup_data = lifegroup_list.get(position).getName();
        holder.setLifegroupText(lifegroup_data);

        lifegroup_image_url = lifegroup_list.get(position).getImage_url();
        lifegroup_thumbUri = lifegroup_list.get(position).getImage_thumb();

        holder.setLifegroupImage(lifegroup_image_url, lifegroup_thumbUri);

        //username and userimage view method
        String user_id = lifegroup_list.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(((HomeMainActivity) context),new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    String userName = task.getResult().getString("username");
                    String userImage = task.getResult().getString("image");

                    holder.setUserData(userName, userImage);
                }
                else
                    {
                        //Firebase Exception
                    }
            }
        });
        try {
            //Date posted method
            long milliseconds = lifegroup_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
            holder.setTime(dateString);
        }
            catch (Exception e)
            {
                Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        //Get Likes Count
        firebaseFirestore.collection("Posts/" + lgPostId + "/Likes").addSnapshotListener(((HomeMainActivity) context), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e)
            {
                if(!documentSnapshots.isEmpty())
                {
                    int count = documentSnapshots.size();
                    holder.updateLikesCount(count);
                } else
                    {
                    holder.updateLikesCount(0);
                    }
            }
        });

        //Get likes method
        firebaseFirestore.collection("Posts/" + lgPostId + "/Likes").document(currentUserId).addSnapshotListener(((HomeMainActivity) context),new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(documentSnapshot.exists())
                {
                    holder.leafLikeViewBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_leaf_green));
                }
                else
                    {
                        holder.leafLikeViewBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_leaf_gray));
                    }
            }
        });

        //Likes method
        holder.leafLikeViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore.collection("Posts/" + lgPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(((HomeMainActivity) context),new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists())
                        {
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Posts/" + lgPostId + "/Likes").document(currentUserId).set(likesMap);
                        }
                        else
                            {
                                firebaseFirestore.collection("Posts/" + lgPostId + "/Likes").document(currentUserId).delete();
                            }
                    }
                });
            }
        });

        holder.lgCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("lg_post_id", lgPostId);
                context.startActivity(commentIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lifegroup_list.size();
    }

    //this constructor requires for view holder
    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView attendeeView, lifegroupView, usernameView, postdateView;
        private CircleImageView userimageView;

        private ImageView liferoupImageView;

        private ImageView leafLikeViewBtn;
        private TextView leafLikeCount;

        private ImageView lgCommentBtn;

        public ViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;

            leafLikeViewBtn = mView.findViewById(R.id.btn_gray_leaf);
            lgCommentBtn = mView.findViewById(R.id.btn_comment);
        }

        public void setUserData(String userText, String userImage)
        {
            usernameView = mView.findViewById(R.id.card_view_username_holder);
            usernameView.setText(userText);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);

            userimageView = mView.findViewById(R.id.card_view_userimage_holder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(userImage).into(userimageView);
        }
        public void setAttendeeText(String attendeeText)
        {
            attendeeView = mView.findViewById(R.id.card_view_attendees_holder);
            attendeeView.setText(attendeeText);
        }

        public void setLifegroupText(String lifegroupText)
        {
            lifegroupView = mView.findViewById(R.id.card_view_lifegroup_holder);
            lifegroupView.setText(lifegroupText);
        }

        public void setLifegroupImage(String downloadUri , String thumbUri)
        {
            liferoupImageView = mView.findViewById(R.id.card_view_imageview_holder);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(liferoupImageView);
        }
        public void setTime(String date)
        {
            postdateView = mView.findViewById(R.id.card_view_postdate_holder);
            postdateView.setText(date);
        }

        public void updateLikesCount(int count)
        {
            leafLikeCount =mView.findViewById(R.id.like_leaf_count);
            leafLikeCount.setText(count + " Likes");
        }
    }
}
