package com.example.neuralstyle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button              boutonCandy;
    Button              boutonComposition;
    Button              boutonFeathers;
    Button              boutonLamuse;
    Button              boutonMozaic;
    Button				boutonScream;
    Button				boutonStary;
    Button				boutonUdnie;
    Button				boutonWave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activite_principale);
        boutonCandy = installButton("candy.t7",R.id.boutonCandy);
        boutonComposition = installButton("composition_vii.t7",R.id.boutonComposition);
        boutonFeathers = installButton("feathers.t7",R.id.boutonFeathers);
        boutonLamuse = installButton("la_muse.t7",R.id.boutonMuse);
        boutonMozaic = installButton("mosaic.t7",R.id.boutonMozaic);
        boutonScream = installButton("the_scream.t7",R.id.boutonScream);
        boutonStary = installButton("starry_night.t7",R.id.boutonStary);
        boutonUdnie = installButton("udnie.t7",R.id.boutonUdnie);
        boutonWave = installButton("the_wave.t7",R.id.boutonWave);
     }
    // Upload file to storage and return a path.
    protected  Button installButton(final String nom, int idRes) {
        Button boutonDNN = (Button) findViewById(idRes);
        boutonDNN.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent ldnn = new Intent(MainActivity.this,activite_dnn.class);
                Bundle b = new Bundle();
                b.putString("model",nom);
                ldnn.putExtras(b);
                startActivity(ldnn);
            }
        });
        return boutonDNN;
    }
    private static final String[] classNames = {"background",
            "aeroplane", "bicycle", "bird", "boat",
            "bottle", "bus", "car", "cat", "chair",
            "cow", "diningtable", "dog", "horse",
            "motorbike", "person", "pottedplant",
            "sheep", "sofa", "train", "tvmonitor"};
}
