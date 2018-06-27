package com.example.ben.threadcounter;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    Button btnStart;
    Button btnPause;
    Button btnClear;
    TextView txtCounter;
    TextView txtText;

    Handler handle = null;
    Runnable runnableright = null;
    Runnable runnableleft = null;

    int digitleft = 0;
    int digitright = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnClear = (Button) findViewById(R.id.btnClear);
        txtCounter = (TextView) findViewById(R.id.txtCounter);
        txtText = (TextView) findViewById(R.id.txtText);

        handle = new Handler();

        // sağ digit 9 olduğunda 9 yazılıp ardından sol thread çalıştırılıyor ve sol digit 1 artırılıp, sağ digit 0 olarak ayarlanıyor.
        runnableleft = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        digitleft++;
                        digitright = 0;
                        txtCounter.setText(String.valueOf(digitleft) + String.valueOf(digitright));
                        txtText.setText("First Thread is now Working,\n Second Thread is set to Zero!");
                    }
                });
            }
        };

        final Thread threadleft = new Thread(runnableleft);


        runnableright = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // sağ ve sol digitler 9 olduğunda threadler durduruluyor.
                        if (digitright == 9 && digitleft == 9){
                            txtText.setText("Counter is Stopped!");
                            handle.removeCallbacks(runnableright);
                        }
                        // sağ digit 9 olduğunda sol digit çalıştırılıyor.
                        // 2. ve daha sonraki çağırılmalarda else yapısıyla sol thread çağırılıyor.
                        else if (digitright == 9) {
                            if (threadleft.getState() == Thread.State.NEW)
                            {
                                threadleft.start();
                            }
                            else {
                                Executors.newSingleThreadExecutor().execute(threadleft);
                            }

                        }
                        // sağ thread çalıştırılıyor
                        else {
                            txtText.setText("First Thread Waits,\n Second Thread is Working!");
                            digitright++;
                        }
                        // digitler yazılıyor
                        txtCounter.setText(String.valueOf(digitleft) + String.valueOf(digitright));
                        handle.postDelayed(runnableright, 1000);
                    }
                });
            }
        };

        final Thread threadright = new Thread(runnableright);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // thread ilk kez başlatılıyorsa
                if (threadright.getState() == Thread.State.NEW)
                {
                    threadright.start();
                }
                // pause butonuna tıklandıktan sonra tekrar çalıştırılıyorsa
                else {
                    Executors.newSingleThreadExecutor().execute(threadright);
                }
            }
        });

        // pause butonuna tıklandığında
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle.removeCallbacks(runnableright);
                txtText.setText("Counter Paused!");
            }
        });

        // clear butonuna tıklandığıda digitler 0 olarak ayarlanıyor ve thread durduruluyor
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                digitright = 0;
                digitleft = 0;
                txtCounter.setText(String.valueOf(digitleft) + String.valueOf(digitright));
                txtText.setText("Counter Cleared!");
                handle.removeCallbacks(runnableright);
            }
        });
    }
}
