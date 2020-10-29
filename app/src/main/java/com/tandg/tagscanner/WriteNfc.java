package com.tandg.tagscanner;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

public class WriteNfc extends AppCompatActivity {

    private static final String TAG = WriteNfc.class.getSimpleName();

    TextView edtStartingText, edtStartingNumber, edtInterval;
    Button btnStart;
    TextInputLayout inputStartingText, inputStartingNumber, inputStartingInterval;
    String mineTypeData = "text/plain";

    String incrementRow;

    String text, number, interval, mergedData;

    boolean mWriteMode = false;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    boolean isScanningStopped = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_nfc);

        initResources();

    }

    public void initResources() {

        ((TextView) findViewById(R.id.edt_starting_text)).setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        edtInterval = findViewById(R.id.edt_interval_increase);

        ((Button) findViewById(R.id.btnStartScanning)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                validateStartingText();


            }



        });


    }

    private void enableTagWriteMode() {
        mWriteMode = true;
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] mWriteTagFilters = new IntentFilter[]{tagDetected};
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }

    private void disableTagWriteMode() {
        mWriteMode = false;
        mNfcAdapter.disableForegroundDispatch(this);

        Log.e(TAG, "Foreground is disabled and status of scanning : "+isScanningStopped );
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onNewIntent(Intent intent) {
        // Tag writing mode
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefRecord record = NdefRecord.createMime(mergedData, mergedData.getBytes());
            NdefMessage message = new NdefMessage(new NdefRecord[]{record});



            if (writeTag(message, detectedTag)) {

                String intIncrease = ((TextView) findViewById(R.id.edt_interval_increase)).getText().toString();

                int rowNum = Integer.parseInt(intIncrease) + Integer.parseInt(number);

                incrementRow = Integer.toString(rowNum);

                ((TextView) findViewById(R.id.edt_starting_number)).setText(incrementRow);

                Log.e(TAG, "onNewIntent: "+isScanningStopped );

            }
        }
    }


    private void validateStartingText() {

        text = ((TextView) findViewById(R.id.edt_starting_text)).getText().toString().trim();

        Log.e(TAG, "validateStartingText: "+text );

        if (text != null && text.length() > 0) {

            ((TextInputLayout) findViewById(R.id.input_starting_text)).setErrorEnabled(false);
            validateStartingNumber();

        } else {

            ((TextInputLayout) findViewById(R.id.input_starting_text)).setErrorEnabled(true);

        }

    }

    private void validateStartingNumber() {

        number = ((TextView) findViewById(R.id.edt_starting_number)).getText().toString().trim();

        if (number != null && number.length() > 0) {

            ((TextInputLayout) findViewById(R.id.input_starting_number)).setErrorEnabled(false);
            mergedData = text + "" + number;
            Log.e(TAG, "validateStartingNumber: " + mergedData);
            validateInterval();

        } else {

            ((TextInputLayout) findViewById(R.id.input_starting_number)).setErrorEnabled(true);
        }
    }

    private void validateInterval() {

        interval = ((TextView) findViewById(R.id.edt_interval_increase)).getText().toString().trim();

        if (interval != null && interval.length() > 0) {

            ((TextInputLayout) findViewById(R.id.input_starting_number)).setErrorEnabled(false);
            initializingAdapter();

        } else {

            ((TextInputLayout) findViewById(R.id.input_starting_number)).setErrorEnabled(true);
        }
    }

    private void initializingAdapter() {

        isScanningStopped = false;

        mNfcAdapter = NfcAdapter.getDefaultAdapter(WriteNfc.this);
        mNfcPendingIntent = PendingIntent.getActivity(WriteNfc.this, 0,
                new Intent(WriteNfc.this, WriteNfc.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        enableTagWriteMode();

        Utils.hideKeyboard(WriteNfc.this);

        AlertDialog.Builder builder = new AlertDialog.Builder(WriteNfc.this);
        builder.setTitle("Touch tag to write");
        builder.setMessage("Scanning Tag ID : "+text+""+number+"\n"+"Press continue after scanning");
        builder.setCancelable(false);
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        disableTagWriteMode();
                        isScanningStopped = false;

                        if(!isScanningStopped){

                            validateStartingText();

                        }

                    }

        });
        builder.setNegativeButton("Stop Scanning", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                disableTagWriteMode();
                isScanningStopped = true;

            }

        });


        final AlertDialog dialog = builder.create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog1) {

                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red_A700));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.green_900));


            }
        });

        dialog.show();
    }

    /*
     * Writes an NdefMessage to a NFC tag
     */
    public boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(getApplicationContext(),
                            "Error: tag not writable",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(getApplicationContext(),
                            "Error: tag too small",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                ndef.writeNdefMessage(message);
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        return true;
                    } catch (IOException e) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }


}
