package com.tandg.tagscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;

public class nfcActivity extends AppCompatActivity implements View.OnClickListener {


    Button btnRead;
    Button btnWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        //ButterKnife.bind(this);

        initResources();
    }

    private void initResources() {

        btnWrite = findViewById(R.id.btnWriteId);
        btnRead = findViewById(R.id.btnReadId);

        btnRead.setOnClickListener(this);
        btnWrite.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.btnReadId:

                Intent i = new Intent(this, ReadNfc.class);
                startActivity(i);


                break;


            case R.id.btnWriteId:

                Intent iin = new Intent(this,WriteNfc.class);
                startActivity(iin);

                break;
        }

    }
}
