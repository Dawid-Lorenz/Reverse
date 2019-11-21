package com.example.reverse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button players = (Button) findViewById(R.id.player_btn);

        Button aiBtn = (Button) findViewById(R.id.ai_btn);

        players.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this, Players.class));
            }
        });

        aiBtn.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(MainActivity.this, minimax.class));
             }
        });
    }
}
