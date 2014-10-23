package com.dam.tuner;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

public class AudioRecorder extends AsyncTask<Void, double[], Void>
{
    public static final int frequency = 8000;
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    public static final int blockSize = 256;
    public static final int NUM_ITERATIONS = 1;

    boolean started = true;

    private FFT transformador;
    private Main main;

    public AudioRecorder (Main m)
    {
        main = m;
        transformador = new FFT(blockSize);
    }

    protected Void doInBackground(Void... arg0)
    {
        try
        {
            // int bufferSize = AudioRecord.getMinBufferSize(frequency,
            // AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            int bufferSize = AudioRecord.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding);

            AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC, frequency,
                    channelConfiguration, audioEncoding, bufferSize);

            short[] buffer = new short[blockSize];
            double[] toTransform = new double[blockSize*NUM_ITERATIONS];

            audioRecord.startRecording();

            while (started)
            {
                for (int i = 0; i < NUM_ITERATIONS; i++)
                {
                    int bufferReadResult = audioRecord.read(buffer, 0, blockSize);

                    for (int j = 0; j < blockSize && j < bufferReadResult; j++)
                    {
                        toTransform[(blockSize*i) + j] = (double) buffer[j] / 32768.0; // signed 16 bit (2^15)
                    }
                }

                publishProgress(toTransform);
            }

            audioRecord.stop();

        }
        catch (Throwable t)
        {
            t.printStackTrace();
            Log.e("Tuner", "Recording Failed");
        }

        return null;
    }

    protected void onProgressUpdate(double[]... toTransform)
    {
        double re[] = toTransform[0];
        double im[] = new double[blockSize*NUM_ITERATIONS];

        /*for (int i = 0; i < re.length; i++)
        {
            Log.e("Tuner", i + " -> " + re[i]);
        }*/

        transformador.fft(re, im);
        double frecuencia = transformador.calcularFrecuencia(re, im);

        if (frecuencia < 1000.0 && frecuencia != 0.0)
        {
            main.actualizarFrecuencia(frecuencia);
            Log.e("Tuner", String.valueOf(frecuencia));
        }
    }
}
