package lifegiverappblog.lifelog.com.lifelog.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lifegiverappblog.lifelog.com.lifelog.R;
import lifegiverappblog.lifelog.com.lifelog.comments.CommentsActivity;
import lifegiverappblog.lifelog.com.lifelog.models.Comments;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder>{

    public List<Comments> commentsList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;

    public CommentsRecyclerAdapter(List<Comments> commentsList)
    {
        this.commentsList = commentsList;
    }

    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();

        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsRecyclerAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        String commentMessage = commentsList.get(position).getMessage();
        holder.setComment_message(commentMessage);

        //username and userimage view method
        String user_id = commentsList.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(((CommentsActivity) context),new OnCompleteListener<DocumentSnapshot>() {
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

    }

    @Override
    public int getItemCount() {
        if(commentsList != null)
        {
            return commentsList.size();
        }
        else
            {
            return 0;
            }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView comment_message;
        private CircleImageView userimageView;
        private TextView usernameView;


        public ViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }

        public void setComment_message(String message)
        {
            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);
        }

        public void setUserData(String userText, String userImage)
        {
            usernameView = mView.findViewById(R.id.comment_username);
            usernameView.setText(userText);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);

            userimageView = mView.findViewById(R.id.comment_image);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(userImage).into(userimageView);
        }

    }
}
