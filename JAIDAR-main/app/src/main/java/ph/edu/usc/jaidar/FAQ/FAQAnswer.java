package ph.edu.usc.jaidar.FAQ;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import ph.edu.usc.jaidar.R;

public class FAQAnswer extends AppCompatActivity {

    private TextView textViewQuestion;
    private TextView textViewAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_answer);

        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewAnswer = findViewById(R.id.textViewAnswer);

        String question = getIntent().getStringExtra("question");
        String answer = getIntent().getStringExtra("answer");

        textViewQuestion.setText(question);
        textViewAnswer.setText(answer);
    }
}
