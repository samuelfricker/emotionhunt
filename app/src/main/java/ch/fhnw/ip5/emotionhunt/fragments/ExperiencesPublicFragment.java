package ch.fhnw.ip5.emotionhunt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.fhnw.ip5.emotionhunt.R;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.fragments
 *
 * @author Benjamin Bur
 */

public class ExperiencesPublicFragment extends Fragment {

    public ExperiencesPublicFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_experiences_public, container, false);
    }
}
