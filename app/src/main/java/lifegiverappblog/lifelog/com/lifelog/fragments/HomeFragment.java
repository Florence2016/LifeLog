package lifegiverappblog.lifelog.com.lifelog.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
    private FirebaseAuth firebaseAuth;
    private LifegroupRecyclerAdapter lifegroupRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        lifegroup_list = new ArrayList<>();
        lifegroup_log_post_view = view.findViewById(R.id.post_view_lg_log);

        firebaseAuth = FirebaseAuth.getInstance();

        lifegroupRecyclerAdapter = new LifegroupRecyclerAdapter(lifegroup_list);
        lifegroup_log_post_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        lifegroup_log_post_view.setAdapter(lifegroupRecyclerAdapter);
        lifegroup_log_post_view.setHasFixedSize(true);

        if(firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            lifegroup_log_post_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if(reachedBottom){
                        loadMorePost();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {
                        if (isFirstPageFirstLoad) {
                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            lifegroup_list.clear();
                        }
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String LGPostId = doc.getDocument().getId();
                                LifegroupPost_model lifelgPost = doc.getDocument().toObject(LifegroupPost_model.class).withId(LGPostId);

                                if (isFirstPageFirstLoad) {
                                    lifegroup_list.add(lifelgPost);
                                } else {
                                    lifegroup_list.add(0, lifelgPost);
                                }
                                lifegroupRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                        isFirstPageFirstLoad = false;
                    }
                }
            });
        }
        return view;
    }
    public void loadMorePost(){

        if(firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {
                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String LGPostId = doc.getDocument().getId();
                                LifegroupPost_model lgPost = doc.getDocument().toObject(LifegroupPost_model.class).withId(LGPostId);
                                lifegroup_list.add(lgPost);
                                lifegroupRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });

        }

    }

}
