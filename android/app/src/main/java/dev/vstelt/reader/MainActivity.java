package dev.vstelt.reader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private RecyclerView articles;
    private ArticleAdapter articleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String dbPath = this.getFilesDir() + "/reader.db";

        articles = findViewById(R.id.articles);
        articleAdapter = new ArticleAdapter();

        articles.setAdapter(articleAdapter);
        articles.setLayoutManager(new LinearLayoutManager(this));

        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                articleAdapter.setArticles((Article[]) intent.getSerializableExtra("ARTICLES"));
            }
        }, new IntentFilter("ARTICLES_UPDATED"));

        Reader.populateArticles(this);
    }

    public void addFeedScreen(View view) {
        Intent i = new Intent(view.getContext(), FeedActivity.class);
        this.startActivity(i);
    }
}