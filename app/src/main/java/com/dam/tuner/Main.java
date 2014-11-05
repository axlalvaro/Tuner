package com.dam.tuner;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import java.text.DecimalFormat;

public class Main extends ActionBarActivity implements View.OnClickListener{

    private DrawerLayout drawerLayout;
    private ListView navList, navList2;
    private CharSequence mTitle,drawerTitle;
    private ActionBarDrawerToggle drawerToggle;

    private AudioRecorder recorder;
    private Button boton, botonAnalizador, botonClose;
    private TextView frecuencia, potencia, afinacion;
    private GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);

        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.navList = (ListView) findViewById(R.id.left_drawer);
        this.navList2 = (ListView) findViewById(R.id.left_drawer2);


        // Load an array of options names
        final String[] names = getResources().getStringArray(R.array.array);

        // Set previous array as adapter of the list
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, names);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new DrawerItemClickListener());



        recorder = new AudioRecorder(this);
        frecuencia = (TextView) findViewById(R.id.frecuencia);
        potencia = (TextView) findViewById(R.id.potencia);
        afinacion= (TextView) findViewById(R.id.afinacion);
        boton = (Button) findViewById(R.id.button);
        boton.setOnClickListener(this);
        botonAnalizador = (Button) findViewById(R.id.buttonAnalizador);
        botonAnalizador.setOnClickListener(this);
        botonClose = (Button) findViewById(R.id.buttonClose);
        botonClose.setOnClickListener(this);
        botonClose.setVisibility(View.GONE);



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
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Get text from resources
        mTitle = getResources().getStringArray(R.array.array)[position];

        // Create a new fragment and specify the option to show based on
        // position
       /* Fragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString(MyFragment.KEY_TEXT, mTitle.toString());
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).commit();*/

        afinacion.setText(mTitle);
        // Highlight the selected item, update the title, and close the drawer
        navList.setItemChecked(position, true);

        //      getSupportActionBar().setTitle(mTitle);
        drawerLayout.closeDrawer(navList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
        graphView.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.NONE);

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
            serie.getStyle().color = Color.argb(255, 187, 117, 221);

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
