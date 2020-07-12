package com.example.quizappmvvm.view.details;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.quizappmvvm.R;
import com.example.quizappmvvm.databinding.FragmentDetailsBinding;
import com.example.quizappmvvm.model.QuizModel;
import com.example.quizappmvvm.viewmodel.QuizListViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {
    private  FragmentDetailsBinding detailsBinding;
    private NavController navController;
    private QuizListViewModel detailsViewModel;
    private int position;
    private String quizId;
    private long totalQuestions;
    private QuizModel model;


    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         detailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false);


        return detailsBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        detailsViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);
        detailsViewModel.getLiveData().observe(getViewLifecycleOwner(), quizModels -> {

            Glide.with(getContext())
                    .load(quizModels.get(position).getImage())
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .into(detailsBinding.quizImageViewDetails);
            detailsBinding.listQuizTitleDetails.setText(quizModels.get(position).getName());
            detailsBinding.listQuizDescDetails.setText(quizModels.get(position).getDesc());
            detailsBinding.listQuizDifficultyValue.setText(quizModels.get(position).getLevel());
            detailsBinding.listQuizTotalQuestionsDetailsValue.setText(String.valueOf(quizModels.get(position).getQuestions()));

            //getting quiz id
            quizId = quizModels.get(position).getQuizId();
            //getting total questions
            totalQuestions = quizModels.get(position).getQuestions();

        });


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        assert getArguments() != null;
        position = DetailsFragmentArgs.fromBundle(getArguments()).getPosition();



        detailsBinding.startQuizBtn.setOnClickListener(v -> {
            DetailsFragmentDirections.ActionDetailsFragmentToQuizFragment action = DetailsFragmentDirections.actionDetailsFragmentToQuizFragment();
            action.setPosition(position);
            action.setTotalQuestions(totalQuestions);
            action.setQuizId(quizId);
            navController.navigate(action);

        });



        Log.d("Details Args", "posiotion: " + position);
    }
}
