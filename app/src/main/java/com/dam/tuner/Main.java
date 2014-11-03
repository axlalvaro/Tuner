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
    private TextView frecuencia;
    private GraphView graphView;
    private OrientationEventListener orientationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recorder = new AudioRecorder(this);

        frecuencia = (TextView) findViewById(R.id.frecuencia);
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

                boton.setAlpha(0);
                frecuencia.setAlpha(0);
                botonAnalizador.setAlpha(0);
                botonClose.setAlpha(1);

                crearGrafica();
                recorder.started = true;

                break;

            case R.id.buttonClose:

                boton.setAlpha(1);
                frecuencia.setAlpha(1);
                botonAnalizador.setAlpha(1);
                botonClose.setAlpha(0);

                graphView.setAlpha(0);
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
        DecimalFormat decFormat = new DecimalFormat("#.0");
        String f = decFormat.format(frec);
        frecuencia.setText(f + " Hz");
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
            GraphView.GraphViewData[] datos = new GraphView.GraphViewData[pintar.length];

            for (int i = 0; i < pintar.length; i++)
            {
                datos[i] = new GraphView.GraphViewData(i, pintar[i]);
            }

            serie = new GraphViewSeries(datos);
            serie.getStyle().color = Color.argb(255,187,117,221);

            graphView.addSeries(serie);
        }
    }
}
