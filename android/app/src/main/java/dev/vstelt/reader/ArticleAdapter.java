package dev.vstelt.reader;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private Article[] articles;

    public ArticleAdapter() {
        this.articles = new Article[] {};
    }

    public void setArticles(Article[] articles) {
        this.articles = articles;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card, parent, false);

        return new ArticleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticleAdapter.ViewHolder holder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Article a = articles[position];
        holder.getTitle().setText(a.title);
        holder.getPreview().setText(a.preview);
        holder.getDate().setText(a.published);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ArticleActivity.class);

                i.putExtra("TITLE", a.title);
                i.putExtra("CONTENT", a.content);

                view.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView preview;
        private TextView date;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            title = view.findViewById(R.id.feedName);
            preview = view.findViewById(R.id.feedCardURL);
            date = view.findViewById(R.id.articleDate);
        }

        public TextView getTitle() {
            return title;
        }
        public TextView getPreview() { return preview; }
        public TextView getDate() { return date; }
    }
}
