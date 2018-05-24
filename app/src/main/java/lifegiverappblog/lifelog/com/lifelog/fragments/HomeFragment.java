package lifegiverappblog.lifelog.com.lifelog.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lifegiverappblog.lifelog.com.lifelog.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private RecyclerView lifegroup_log_post_view;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        lifegroup_log_post_view = getActivity().findViewById(R.id.post_view_lg_log);
        // Inflate the layout for this fragment
        return view;
    }

}
