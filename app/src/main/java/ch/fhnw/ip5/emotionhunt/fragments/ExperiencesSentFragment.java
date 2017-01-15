package ch.fhnw.ip5.emotionhunt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.adapter.ExperienceListAdapter;
import ch.fhnw.ip5.emotionhunt.models.SentExperience;

/**
 * Fragment which is required by the experience list activity.
 * This fragment contains all sent experiences.
 */
public class ExperiencesSentFragment extends Fragment {

    public ExperiencesSentFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_experiences_received, container, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        rv.setHasFixedSize(false);

        ExperienceListAdapter adapter = new ExperienceListAdapter(SentExperience.getAll(container.getContext()), container.getContext(), true);
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }
}
