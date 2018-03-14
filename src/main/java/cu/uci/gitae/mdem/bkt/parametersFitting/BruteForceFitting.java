package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.BKT;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Método para el ajuste de parámetros usando fuerza bruta. Basado en el método
 * provisto por Baker en (Baker et al. ,2010, 2011a, 2011b, 2011c).
 *
 * @author angel
 */
public class BruteForceFitting extends FittingMethodImpl {

    private boolean acotadoL0yT;
    private boolean acotadoGyS;
    private List<BKT.Item> items;

    public BruteForceFitting() {
        super();
        this.acotadoGyS = false;
        this.acotadoL0yT = false;
        this.items = new ArrayList<>();
    }

    public BruteForceFitting(boolean acotadoL0yT, boolean acotadoGyS) {
        super();
        this.acotadoL0yT = acotadoL0yT;
        this.acotadoGyS = acotadoGyS;
        this.items = new ArrayList<>();
    }

    public void setAcotadoL0yT(boolean acotadoL0yT) {
        this.acotadoL0yT = acotadoL0yT;
    }

    public void setAcotadoGyS(boolean acotadoGyS) {
        this.acotadoGyS = acotadoGyS;
    }

    public boolean isAcotadoL0yT() {
        return acotadoL0yT;
    }

    public boolean isAcotadoGyS() {
        return acotadoGyS;
    }

    @Override
    public Double[] fitParameter(List<BKT.Item> items) {
        this.items = items;
        
        return null;
    }

    /**
     * Ajusta los parámetros del modelo usando un algoritmo de fuerza bruta, 
     * con una precision de dos cifras decimales.
     * @param habilidad 
     */
    private void ajustarModeloHabilidad(String habilidad) {
        double SSR = 0.0; //suma de cuadrados residuales
        double bestSSR = 9999999.0;
        double bestL0 = 0.01;
        double bestT = 0.01;
        double bestG = 0.01;
        double bestS = 0.01;
        double topG = 0.99;
        double topS = 0.99;
        double topL0 = 0.99;
        double topT = 0.99;
        if (acotadoL0yT) {
            topL0 = 0.85;
            topT = 0.3;
        }
        if (acotadoGyS) {
            topG = 0.3;
            topS = 0.1;
        }
        //TODO me quede aqui
    }
    
    /**
     * Ordena los items primero por la habilidad y luego por los estudiantes.
     * @param items 
     */
    private void sortItems(List<BKT.Item> items){
        Collections.sort(items, (BKT.Item i1, BKT.Item i2)->{
            int valor = i1.getHabilidad().compareTo(i2.getHabilidad());
            if(valor==0){
                return i1.getEstudiante().compareTo(i2.getEstudiante());
            }
            return valor;
        });
    }

}
