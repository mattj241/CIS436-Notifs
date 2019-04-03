package com.android.london.notifs;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
    Integer notificationId = 123456;

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
        createNotificationChannel();
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
            setCountdown.setEnabled(false);
            startCountdown.setEnabled(false);
            Toast.makeText(v.getContext(), "Countdown Started", Toast.LENGTH_SHORT)
                    .show();
            int highestNotif = Integer.parseInt(spinner.getSelectedItem().toString());
            String messageContent = message.getText().toString();
            initiateCountdown(v, highestNotif, messageContent);
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

    //Literally ripped from Android official blog because Android Notifications are REALLY convoluted
    //https://developer.android.com/training/notify-user/build-notification
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("soleChannel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initiateCountdown(final View v, int highestNotif, final String messageContent){
        final List<Integer> notifs = getNotifParatmeters(highestNotif);
        new CountDownTimer(highestNotif * 1000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished);
                secondsLeft = secondsLeft / 1000;
                if (notifs.contains(secondsLeft)){
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(v.getContext(), "soleChannel")
                            .setSmallIcon(R.drawable.ic_notif)
                            .setContentTitle("Counter Notification")
                            .setContentText(String.format("%d seconds to countdown", secondsLeft))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(notificationId, builder.build());
                }
            }

            @Override
            public void onFinish() {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(v.getContext(), "soleChannel")
                        .setSmallIcon(R.drawable.ic_notif)
                        .setContentTitle("Counter Notification")
                        .setContentText(String.format("It's time for %s!", messageContent))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(notificationId, builder.build());
                setCountdown.setEnabled(true);
                startCountdown.setEnabled(false);
            }
        }.start();
    }

    private List<Integer> getNotifParatmeters(int highestNotif){
        List<Integer> notifs = new ArrayList<>();
        for(Integer notif : notifPresets){
            if (notif <= highestNotif) {
                notifs.add(notif);
            }
        }
        return notifs;
    }


}
