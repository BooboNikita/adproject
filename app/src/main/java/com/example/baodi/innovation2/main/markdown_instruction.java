package com.example.baodi.innovation2.main;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baodi.innovation2.MarkDown;
import com.example.baodi.innovation2.R;

import java.io.InputStream;

public class markdown_instruction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markdown_instruction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final TextView textView = (TextView) findViewById(R.id.markdown_instruction);
        assert textView != null;
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        final InputStream stream = getResources().openRawResource(R.raw.mark);

        textView.post(new Runnable() {
            @Override
            public void run() {
//                long time = System.nanoTime();
                Spanned spanned = MarkDown.fromMarkdown(stream, new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        Drawable drawable = new ColorDrawable(Color.LTGRAY);
                        drawable.setBounds(0, 0, textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight(), 400);
                        return drawable;
                    }
                }, textView);
//                long useTime = System.nanoTime() - time;
//                Toast.makeText(getApplicationContext(), "use time:" + useTime, Toast.LENGTH_LONG).show();
                textView.setText(spanned);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}
