package com.example.quizappmvvm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizappmvvm.R;
import com.example.quizappmvvm.callback.OnQuizListItemCLicked;
import com.example.quizappmvvm.model.QuizModel;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private List<QuizModel> quizList;
    private OnQuizListItemCLicked onQuizListItemCLicked;

    public QuizAdapter(OnQuizListItemCLicked onQuizListItemCLicked) {
        this.onQuizListItemCLicked = onQuizListItemCLicked;
    }


    public void setQuizList(List<QuizModel> quizList) {
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_list_item, parent, false);

        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        holder.bindViews(quizList.get(position));
    }

    @Override
    public int getItemCount() {
        if(quizList == null){
            return 0;
        }else
        return quizList.size();
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView quizImage;
        private TextView quizTitleTV, quizDescTV, quizDifficultyTV;
        private Button viewQuizBtn;

        private QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            quizImage = itemView.findViewById(R.id.quiz_image_view);
            quizTitleTV =  itemView.findViewById(R.id.list_quiz_title);
            quizDescTV =  itemView.findViewById(R.id.list_quiz_desc);
            quizDifficultyTV =  itemView.findViewById(R.id.list_quiz_difficulty);
            viewQuizBtn =  itemView.findViewById(R.id.list_item_button);
            viewQuizBtn.setOnClickListener(this);
        }

        private void bindViews(QuizModel model){
            String imageUrl = model.getImage();
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .centerCrop()
                    .into(quizImage);
            quizDescTV.setText(model.getDesc());
            quizDifficultyTV.setText(model.getLevel());
            quizTitleTV.setText(model.getName());
        }

        @Override
        public void onClick(View v) {
            onQuizListItemCLicked.onItemPositionClicked(getAdapterPosition());
        }
    }
}
