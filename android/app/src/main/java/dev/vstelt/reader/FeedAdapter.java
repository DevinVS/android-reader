package dev.vstelt.reader;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private Feed[] feeds;

    public FeedAdapter() {
        this.feeds = new Feed[] {};
    }

    public void setFeeds(Feed[] feeds) {
        this.feeds = feeds;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card, parent, false);

        return new FeedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedAdapter.ViewHolder holder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Feed f = feeds[position];
        holder.getName().setText(f.title);
        holder.getUrl().setText(f.url);
    }

    @Override
    public int getItemCount() {
        return feeds.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView url;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            name = view.findViewById(R.id.feedName);
            url = view.findViewById(R.id.feedCardURL);
        }

        public TextView getName() { return name; }
        public TextView getUrl() { return url; }
    }
}
