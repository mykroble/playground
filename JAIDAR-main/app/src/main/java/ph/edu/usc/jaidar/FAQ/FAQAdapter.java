package ph.edu.usc.jaidar.FAQ;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

import ph.edu.usc.jaidar.R;

public class FAQAdapter extends ArrayAdapter<FAQItem> {

    private Context context;
    private ArrayList<FAQItem> faqList;

    public FAQAdapter(Context context, ArrayList<FAQItem> faqList) {
        super(context, 0, faqList);
        this.context = context;
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.activity_faq_item, parent, false);
        }

        FAQItem currentItem = faqList.get(position);

        TextView textViewQuestion = itemView.findViewById(R.id.textViewQuestion);
        Button buttonViewMore = itemView.findViewById(R.id.buttonViewMore);

        textViewQuestion.setText(currentItem.getQuestion());

        buttonViewMore.setOnClickListener(v -> {
            Intent intent = new Intent(context, FAQAnswer.class);
            intent.putExtra("question", currentItem.getQuestion());
            intent.putExtra("answer", currentItem.getAnswer());
            context.startActivity(intent);
        });

        return itemView;
    }
}
