package com.example.quizappmvvm.view.quiz_list;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quizappmvvm.R;
import com.example.quizappmvvm.adapter.QuizAdapter;
import com.example.quizappmvvm.model.QuizModel;
import com.example.quizappmvvm.viewmodel.QuizListViewModel;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {
    private RecyclerView quizRecyclerView;
    private QuizListViewModel viewModel;
    private QuizAdapter quizAdapter;


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
        quizAdapter = new QuizAdapter();

        quizRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        quizRecyclerView.setHasFixedSize(true);
        quizRecyclerView.setAdapter(quizAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);
        viewModel.getLiveData().observe(getViewLifecycleOwner(), quizModels -> {
            quizAdapter.setQuizList(quizModels);
            quizAdapter.notifyDataSetChanged();

        });

    }
}
