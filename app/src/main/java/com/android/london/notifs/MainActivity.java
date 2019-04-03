package com.android.london.notifs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener{

    TextView time, message;
    Spinner spinner;
    Button setCountdown, startCountdown, reset;
    private int[] notifPresets = {1, 5, 10, 20, 30, 60, 90};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        time = findViewById(R.id.TimeToCountdown);
        message = findViewById(R.id.Message);
        spinner = findViewById(R.id.HighestNotification);
        setCountdown = findViewById(R.id.SetCountDownBtn);
        startCountdown = findViewById(R.id.StartCountdownBtn);
        reset = findViewById(R.id.ResetBtn);
        spinner.setEnabled(false);
        startCountdown.setEnabled(false);

        setCountdown.setOnClickListener(this);
        startCountdown.setOnClickListener(this);
        reset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId())
        {
            case R.id.SetCountDownBtn:
                handleSet(v);
                break;
            case R.id.StartCountdownBtn:
                handleStart(v);
                break;
            case R.id.ResetBtn:
                handleReset(v);
                break;
            default:
        }
    }

    private void handleSet(View v){
        Integer input = null;
        try{
            input = Integer.parseInt(time.getText().toString());
            if (input < 5 || input > 120 || input % 5 != 0){
                Toast.makeText(v.getContext(), "Min: 5, Max: 120, must be divisible by 5", Toast.LENGTH_SHORT)
                        .show();
            }
            else{
                populateSpinner(input);
                reset.setVisibility(View.VISIBLE);
                time.setEnabled(false);
                spinner.setEnabled(true);
                startCountdown.setEnabled(true);
            }
        }
        catch ( Exception e ){
            Toast.makeText(v.getContext(), "Please enter an Integer", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void handleReset(View v){
        reset.setVisibility(View.GONE);
        time.setEnabled(true);
        spinner.setEnabled(false);
        startCountdown.setEnabled(false);
    }

    private void handleStart(View v){
        if (message.getText().toString().isEmpty()){
            Toast.makeText(v.getContext(), "You should enter a message first :)", Toast.LENGTH_SHORT)
                    .show();
        }
        else
        {
            Toast.makeText(v.getContext(), "Countdown Started", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    //https://stackoverflow.com/questions/11920754/android-fill-spinner-from-java-code-programmatically
    private void populateSpinner(int selection){
        List<Integer> spinnerContents = new ArrayList<>();
        for(Integer integer : notifPresets){
            if (selection >= integer){
                spinnerContents.add(integer);
            }
        }
        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, spinnerContents);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }


}
