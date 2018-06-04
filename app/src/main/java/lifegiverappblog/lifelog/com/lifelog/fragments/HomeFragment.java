package lifegiverappblog.lifelog.com.lifelog.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import lifegiverappblog.lifelog.com.lifelog.R;
import lifegiverappblog.lifelog.com.lifelog.adapters.LifegroupRecyclerAdapter;
import lifegiverappblog.lifelog.com.lifelog.models.LifegroupPost_model;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private RecyclerView lifegroup_log_post_view;
    private List<LifegroupPost_model> lifegroup_list;
    private FirebaseFirestore firebaseFirestore;
    private LifegroupRecyclerAdapter lifegroupRecyclerAdapter;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        lifegroup_list = new ArrayList<>();
        lifegroup_log_post_view = view.findViewById(R.id.post_view_lg_log);
        lifegroupRecyclerAdapter = new LifegroupRecyclerAdapter(lifegroup_list);

        lifegroup_log_post_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        lifegroup_log_post_view.setAdapter(lifegroupRecyclerAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for(DocumentChange doc: documentSnapshots.getDocumentChanges())
                {
                    if(doc.getType() == DocumentChange.Type.ADDED)
                    {
                        LifegroupPost_model lifegroup_model = doc.getDocument().toObject(LifegroupPost_model.class);
                        lifegroup_list.add(lifegroup_model);

                        lifegroupRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
