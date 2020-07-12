package com.example.quizappmvvm.view.quiz_list;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.Navigator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.example.quizappmvvm.R;
import com.example.quizappmvvm.adapter.QuizAdapter;
import com.example.quizappmvvm.callback.OnQuizListItemCLicked;
import com.example.quizappmvvm.model.QuizModel;
import com.example.quizappmvvm.viewmodel.QuizListViewModel;
import com.google.android.material.internal.NavigationMenu;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements OnQuizListItemCLicked {
    private RecyclerView quizRecyclerView;
    private QuizListViewModel viewModel;
    private QuizAdapter quizAdapter;
    private ProgressBar quizListProgress;
    private Animation fadeInAnim;
    private Animation fadeOutAnim;
    private NavController listNavController;


    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        quizRecyclerView = view.findViewById(R.id.quiz_recycler);
        quizListProgress = view.findViewById(R.id.quiz_list_progress);

        quizAdapter = new QuizAdapter(this);
        quizRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        quizRecyclerView.setHasFixedSize(true);
        quizRecyclerView.setAdapter(quizAdapter);

        fadeInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fadeOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);

        listNavController = Navigation.findNavController(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);
        viewModel.getLiveData().observe(getViewLifecycleOwner(), quizModels -> {

            quizRecyclerView.startAnimation(fadeInAnim);
            quizListProgress.startAnimation(fadeOutAnim);
            quizAdapter.setQuizList(quizModels);
            quizAdapter.notifyDataSetChanged();
            quizListProgress.setVisibility(View.INVISIBLE);

        });
    }

    @Override
    public void onItemPositionClicked(int position) {
        ListFragmentDirections.ActionListFragmentToDetailsFragment action = ListFragmentDirections.actionListFragmentToDetailsFragment();
        action.setPosition(position);
        listNavController.navigate(action);
    }
}
