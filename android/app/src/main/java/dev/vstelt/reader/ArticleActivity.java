package dev.vstelt.reader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class ArticleActivity extends AppCompatActivity {

    final private static String PREFIX = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Intent intent = getIntent();

        String title = intent.getStringExtra("TITLE");
        String content = intent.getStringExtra("CONTENT");

        TextView articleTitle = findViewById(R.id.articleTitle);
        WebView articleContent = findViewById(R.id.articleContent);

        articleContent.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        articleTitle.setText(title);
        String html = PREFIX + content;
        articleContent.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
    }
}