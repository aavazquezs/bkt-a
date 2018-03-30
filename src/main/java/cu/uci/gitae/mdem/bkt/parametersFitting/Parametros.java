package cu.uci.gitae.mdem.bkt.parametersFitting;

import java.util.Random;

/**
 *
 * @author angel
 */
public class Parametros {
    private double l0;
    private double t;
    private double g;
    private double s;

    public Parametros() {
    }

    public Parametros(double l0, double t, double g, double s) {
        this.l0 = l0;
        this.t = t;
        this.g = g;
        this.s = s;
    }

    /**
     * Inicializa de forma aleatoria el conjunto de parámetros.
     */
    public void randomInit(){
        Random rand = new Random();
        this.l0 = rand.nextDouble();
        this.t = rand.nextDouble();
        this.s = rand.nextDouble();
        this.g = rand.nextDouble();
    }
    
    public double getL0() {
        return l0;
    }

    public void setL0(double l0) {
        this.l0 = l0;
    }

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getS() {
        return s;
    }

    public void setS(double s) {
        this.s = s;
    }
    
}
