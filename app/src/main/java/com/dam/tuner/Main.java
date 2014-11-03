package com.dam.tuner;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity implements View.OnClickListener
{
    AudioRecorder recorder;
    Button boton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recorder = new AudioRecorder(this);

        boton = (Button)findViewById(R.id.button);
        boton.setOnClickListener(this);
    }

    public void onClick (View view)
    {
        switch (view.getId())
        {
            case R.id.button:
                if (recorder.started)
                {
                    recorder.started = false;
                    boton.setText("START");
                }
                else
                {
                    recorder.started = true;
                    boton.setText("STOP");
                }

                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onResume ()
    {
        super.onResume();

        recorder.execute();
    }


    public void actualizarFrecuencia(double frecuencia)
    {
        TextView textView = (TextView) findViewById(R.id.frecuencia);
        textView.setText(frecuencia + " Hz");
    }
}
