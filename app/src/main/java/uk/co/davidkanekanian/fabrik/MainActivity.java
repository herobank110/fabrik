package uk.co.davidkanekanian.fabrik;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.joml.Vector2f;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Vector2f a = new Vector2f(2.25f, 0.f);
        Vector2f b = new Vector2f(5.25f, 10.f);
        a.add(b);

        TextView textView = findViewById(R.id.test_text);
        textView.setText(a.toString(NumberFormat.getNumberInstance()));
    }
}