package com.dam.tuner;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jjoe64.graphview.*;

import java.text.DecimalFormat;

public class Main extends Activity implements View.OnClickListener
{
    private AudioRecorder recorder;
    private Button boton, botonAnalizador, botonClose;
    private TextView frecuencia, potencia;
    private GraphView graphView;
    private OrientationEventListener orientationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recorder = new AudioRecorder(this);

        frecuencia = (TextView) findViewById(R.id.frecuencia);
        potencia = (TextView) findViewById(R.id.potencia);
        boton = (Button) findViewById(R.id.button);
        boton.setOnClickListener(this);
        botonAnalizador = (Button) findViewById(R.id.buttonAnalizador);
        botonAnalizador.setOnClickListener(this);
        botonClose = (Button) findViewById(R.id.buttonClose);
        botonClose.setOnClickListener(this);
        botonClose.setAlpha(0);
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

            case R.id.buttonAnalizador:

                boton.setVisibility(View.GONE);
                //frecuencia.setVisibility(View.GONE);
                botonAnalizador.setVisibility(View.GONE);
                botonClose.setVisibility(View.VISIBLE);


                crearGrafica();
                recorder.started = true;

                break;

            case R.id.buttonClose:

                boton.setVisibility(View.VISIBLE);
                frecuencia.setVisibility(View.VISIBLE);
                botonAnalizador.setVisibility(View.VISIBLE);
                botonClose.setVisibility(View.GONE);

                graphView.setVisibility(View.GONE);
                graphView = null;
                recorder.started = false;
                boton.setText("START");

                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
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

        if (recorder.getStatus() != AsyncTask.Status.RUNNING)
            recorder.execute();
    }


    public void actualizarFrecuencia(double frec)
    {
        frecuencia.setText(format2Decimal(frec) + " Hz");
    }

    public void actualizarPotencia(double pot)
    {
        potencia.setText(format2Decimal(pot) + " dB");
    }

    private void crearGrafica()
    {
        graphView = new LineGraphView( this, "Analizador" );
        graphView.setShowVerticalLabels(false);
        graphView.setScalable(true);
        graphView.setScrollable(true);
        graphView.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.VERTICAL);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        layout.addView(graphView, 0);
    }

    public void actualizarGrafica(double[] pintar)
    {
        if (graphView != null)
        {
            graphView.removeAllSeries();

            GraphViewSeries serie;
            GraphView.GraphViewData[] datos = new GraphView.GraphViewData[pintar.length / 4];

            for (int i = 0; i < pintar.length / 4; i++)
            {
                datos[i] = new GraphView.GraphViewData(i, pintar[i]);
            }

            serie = new GraphViewSeries(datos);
            serie.getStyle().color = Color.argb(255,187,117,221);

            graphView.addSeries(serie);
        }
    }

    private String format2Decimal(double value)
    {
        DecimalFormat decFormat = new DecimalFormat("#.00");
        String f = decFormat.format(value);

        return f;
    }
}
