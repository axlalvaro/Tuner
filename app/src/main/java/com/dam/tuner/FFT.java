package com.dam.tuner;

public class FFT
{
    int n, m;

    double[] cos;
    double[] sin;

    public FFT(int n)
    {
        this.n = n;
        this.m = (int) (Math.log(n) / Math.log(2));

        // Make sure n is a power of 2
        if (n != (1 << m))
            throw new RuntimeException("FFT length must be power of 2");

        cos = new double[n / 2];
        sin = new double[n / 2];

        for (int i = 0; i < n / 2; i++)
        {
            cos[i] = Math.cos(-2 * Math.PI * i / n);
            sin[i] = Math.sin(-2 * Math.PI * i / n);
        }
    }

    public void fft(double[] x, double[] y)
    {
        int i, j, k, n1, n2, a;
        double c, s, t1, t2;

        // Bit-reverse
        j = 0;
        n2 = n / 2;
        for (i = 1; i < n - 1; i++)
        {
            n1 = n2;
            while (j >= n1)
            {
                j = j - n1;
                n1 = n1 / 2;
            }
            j = j + n1;

            if (i < j)
            {
                t1 = x[i];
                x[i] = x[j];
                x[j] = t1;
                t1 = y[i];
                y[i] = y[j];
                y[j] = t1;
            }
        }

        // FFT
        n1 = 0;
        n2 = 1;

        for (i = 0; i < m; i++)
        {
            n1 = n2;
            n2 = n2 + n2;
            a = 0;

            for (j = 0; j < n1; j++)
            {
                c = cos[a];
                s = sin[a];
                a += 1 << (m - i - 1);

                for (k = j; k < n; k = k + n2)
                {
                    t1 = c * x[k + n1] - s * y[k + n1];
                    t2 = s * x[k + n1] + c * y[k + n1];
                    x[k + n1] = x[k] - t1;
                    y[k + n1] = y[k] - t2;
                    x[k] = x[k] + t1;
                    y[k] = y[k] + t2;
                }
            }
        }
    }

    public double calcularFrecuenciaYPot(double[] re, double[] im, double[] mag, double[] pot)
    {
        double frecuencia = 0.0;

        int indiceSuperior = 0;
        double valorSuperior = Math.sqrt(re[0]*re[0] + im[0]*im[0]);

        mag[0] = valorSuperior;

        for (int i = 1; i < re.length / 2; i++)
        {
            double modulo = Math.sqrt((re[i]*re[i]) + (im[i]*im[i]));

            mag[i] = modulo;

            if (modulo > valorSuperior)
            {
                valorSuperior = modulo;
                indiceSuperior = i;
            }
        }

        frecuencia = ((double)AudioRecorder.frequency/((double)re.length)) * (double)indiceSuperior;
        pot[0] = 20*Math.log10(mag[indiceSuperior]);

        return frecuencia;
    }


    public double[] rectangular(double[] datos)
    {
        int N = datos.length;

        for (int k = 0; k < N; k++)
        {
            datos[k] = datos[k] * (1.0);
        }

        return datos;
    }

    public double[] hamming(double[] datos)
    {
        double A0 = 0.53836;
        double A1 = 0.46164;
        int N = datos.length;

        for (int k = 0; k < N; k++)
        {
            datos[k] = datos[k] * (A0-A1 * Math.cos(2 * Math.PI * k / (N - 1)));
        }

        return datos;
    }

    public double[] blackman(double[] datos)
    {
        int N = datos.length;

        for (int k = 0; k < N; k++)
        {
            datos[k] = datos[k] * ( 0.42-0.5 * Math.cos((2*Math.PI*k)/(N-1)) + 0.08*Math.cos((4*Math.PI*k)/(N-1)));
        }

        return datos;
    }
}
