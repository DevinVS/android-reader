package dev.vstelt.reader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class FeedActivity extends AppCompatActivity {

    private EditText url;
    private RecyclerView feeds;
    private FeedAdapter feedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        url = findViewById(R.id.feedURL);
        feeds = findViewById(R.id.feeds);

        feedAdapter = new FeedAdapter();

        feeds.setAdapter(feedAdapter);
        feeds.setLayoutManager(new LinearLayoutManager(this));

        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                feedAdapter.setFeeds((Feed[]) intent.getSerializableExtra("FEEDS"));
            }
        }, new IntentFilter("FEEDS_UPDATED"));

        Reader.populateFeeds(this);
    }

    public void importFeed(View v) {
        Reader.importFeed(v.getContext(), url.getText().toString());
    }
}