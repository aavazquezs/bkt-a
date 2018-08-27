package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.spark.sql.Dataset;

/**
 * Método para el ajuste de parámetros usando fuerza bruta. Basado en el método
 * provisto por Baker en (Baker et al. ,2010, 2011a, 2011b, 2011c).
 *
 * @author angel
 */
public class BruteForceFitting extends FittingMethodImpl {

    private boolean acotadoL0yT;
    private boolean acotadoGyS;
    //private boolean estimacionLnMenos1;
    private Map<String, Double> top = new HashMap<>();
    private List<Item> items;

    public BruteForceFitting() {
        super();
        this.acotadoGyS = false;
        this.acotadoL0yT = false;
        //this.estimacionLnMenos1 = false;
        this.items = new ArrayList<>();
    }

    public BruteForceFitting(boolean acotadoL0yT, boolean acotadoGyS) {
        this.acotadoL0yT = acotadoL0yT;
        this.acotadoGyS = acotadoGyS;
//        this.estimacionLnMenos1 = estimacionLnMenos1;
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

//    public boolean isEstimacionLnMenos1() {
//        return estimacionLnMenos1;
//    }
//
//    public void setEstimacionLnMenos1(boolean estimacionLnMenos1) {
//        this.estimacionLnMenos1 = estimacionLnMenos1;
//    }

    /**
     * Ajusta los parametros del algoritmo (L0, T, G, S) para cada habilidad.
     * @param items Conjunto de items 
     * @return Map<String, Parametros> el conjunto de parametros ajustados para 
     * cada habilidad.
     */
    @Override
    public Map<String, Parametros> fitParameters(List<Item> items) {
        this.items = items;
        this.sortItems(items);
        Map<String, Parametros> resultado = new HashMap<>();
        List<String> habilidades = this.getHabilidades(this.items);
        habilidades.forEach(h->{
            Parametros actual = this.ajustarModeloHabilidad(h);
            resultado.put(h, actual);
        });
        return resultado;
    }

    /**
     * Ajusta los parámetros del modelo usando un algoritmo de fuerza bruta, con
     * una precision de dos cifras decimales.
     *
     * @param habilidad
     * @return 
     */
    @Override
    protected Parametros ajustarModeloHabilidad(String habilidad) {
        List<Item> itemsActuales = items.stream().parallel()
                .filter(t -> {
                    return t.getHabilidad().equals(habilidad);
                })
                .collect(Collectors.toList());
        
        double SSR, bestSSR = Double.MAX_VALUE;
        Parametros best = new Parametros(0.01, 0.01, 0.01, 0.01);
        
        if(acotadoL0yT){
            top.put("L0", 0.85);
            top.put("T", 0.3);
        }else{
            top.put("L0", 0.99);
            top.put("T", 0.99);
        }
        if (acotadoGyS) {
            top.put("G", 0.3);
            top.put("S", 0.1);
        }else{
            top.put("G", 0.99);
            top.put("S", 0.99);
        }
        
        Parametros paramsActual;
        
        for (double L0 = 0.1; L0 <= top.get("L0"); L0 += 0.1) {
            for (double T = 0.1; T <= top.get("T"); T += 0.1) {
                for (double G = 0.1; G <= top.get("G"); G += 0.1) {
                    for (double S = 0.1; S <= top.get("S"); S += 0.1) {
                        paramsActual = new Parametros(L0, T, G, S);
                        SSR = this.findSSR(paramsActual, itemsActuales);
                        if (SSR < bestSSR) {
                            bestSSR = SSR;
                            best = new Parametros(paramsActual);
                        }
                    }
                }
            }
        }
        
        //para buscar mas precision
        
        Parametros start = new Parametros(best);

        for (double l0 = start.getL0() - 0.09; l0 <= start.getL0() + 0.09 && l0 <= top.get("L0"); l0 += 0.01) {
            for (double t = start.getT() - 0.09; t <= start.getT() + 0.09 && t <= top.get("T"); t += 0.01) {
                for (double g = start.getG() - 0.09; g <= start.getG() + 0.09 && g <= top.get("G"); g += 0.01) {
                    for (double s = start.getS() - 0.09; s <= start.getS() + 0.09 && s <= top.get("S"); s += 0.01) {
                        paramsActual = new Parametros(l0, t, g, s);
                        SSR = findSSR(paramsActual, itemsActuales);
                        if (SSR < bestSSR) {
                            bestSSR = SSR;
                            best = new Parametros(paramsActual);
                        }
                    }
                }
            }
        }
        
        return best;
    }

    /**
     * Calcula la suma cuadrada del los residuales entre el likelihood y el valor de L
     * @param l0
     * @param t
     * @param g
     * @param s
     * @param itemsActuales
     * @return 
     */
    private double findSSR(Parametros param, List<Item> itemsActuales) {
        double SSR = 0.0;
        String estudianteAnterior = items.get(0).getEstudiante();
        double prevL = 0.0;
        double likelihoodCorrect, prevLGivenResult;

        for (Item item : itemsActuales) {
            if (!item.getEstudiante().equalsIgnoreCase(estudianteAnterior)) {
                prevL = param.getL0();
                estudianteAnterior = item.getEstudiante();
            }

            likelihoodCorrect = prevL * (1.0 - param.getS()) + (1.0 - prevL) * param.getG();
            
            double respuestaActual = item.isCorrecto() ? 1.0 : 0.0;
            SSR += (respuestaActual - likelihoodCorrect) * (respuestaActual - likelihoodCorrect);
            
            if (item.isCorrecto()) {
                prevLGivenResult = (prevL * (1.0 - param.getS())) / (prevL * (1 - param.getS()) + (1.0 - prevL) * param.getG());
            } else {
                prevLGivenResult = (prevL * param.getS()) / (prevL * param.getS() + (1.0 - prevL) * (1.0 - param.getG()));
            }

            prevL = prevLGivenResult + (1.0 - prevLGivenResult) * param.getT();
        }

        return SSR;
    }

    @Override
    public Map<String, Parametros> fitParameters(Dataset<Item> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
