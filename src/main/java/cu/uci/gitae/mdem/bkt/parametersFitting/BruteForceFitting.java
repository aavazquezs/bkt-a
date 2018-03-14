package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.BKT;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Método para el ajuste de parámetros usando fuerza bruta. Basado en el método
 * provisto por Baker en (Baker et al. ,2010, 2011a, 2011b, 2011c).
 *
 * @author angel
 */
public class BruteForceFitting extends FittingMethodImpl {

    private boolean acotadoL0yT;
    private boolean acotadoGyS;
    private boolean estimacionLnMenos1;
    private List<BKT.Item> items;

    public BruteForceFitting() {
        super();
        this.acotadoGyS = false;
        this.acotadoL0yT = false;
        this.estimacionLnMenos1 = false;
        this.items = new ArrayList<>();
    }

    public BruteForceFitting(boolean acotadoL0yT, boolean acotadoGyS, boolean estimacionLnMenos1) {
        this.acotadoL0yT = acotadoL0yT;
        this.acotadoGyS = acotadoGyS;
        this.estimacionLnMenos1 = estimacionLnMenos1;
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

    public boolean isEstimacionLnMenos1() {
        return estimacionLnMenos1;
    }

    public void setEstimacionLnMenos1(boolean estimacionLnMenos1) {
        this.estimacionLnMenos1 = estimacionLnMenos1;
    }

    /**
     * Ajusta los parametros del algoritmo (L0, T, G, S) para cada habilidad.
     * @param items Conjunto de items 
     * @return Map<String, Parametros> el conjunto de parametros ajustados para 
     * cada habilidad.
     */
    @Override
    public Map<String, Parametros> fitParameters(List<BKT.Item> items) {
        this.items = items;
        this.sortItems(items);
        Map<String, Parametros> resultado = new HashMap<>();
        List<String> habilidades = this.getHabilidades();
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
     */
    private Parametros ajustarModeloHabilidad(String habilidad) {
        double SSR = 0.0; //suma de cuadrados residuales
        double bestSSR = 9999999.0;
        this.pL0 = 0.01;
        this.pT = 0.01;
        this.pG = 0.01;
        this.pS = 0.01;
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
        List<BKT.Item> itemsActuales = items
                .stream()
                .filter(t -> {
                    return t.getHabilidad().equals(habilidad);
                })
                .collect(Collectors.toList());

        for (double L0 = 0.01; L0 <= topL0; L0 += 0.01) {
            for (double T = 0.01; T <= topT; T += 0.01) {
                for (double G = 0.01; G <= topG; G += 0.01) {
                    for (double S = 0.01; S <= topS; S += 0.01) {
                        SSR = this.findSSR(L0, T, G, S, itemsActuales);
                        if (SSR < bestSSR) {
                            bestSSR = SSR;
                            bestL0 = L0;
                            bestT = T;
                            bestS = S;
                            bestG = G;
//                            this.pL0 = L0;
//                            this.pT = T;
//                            this.pS = S;
//                            this.pG = G;
                        }
                    }
                }
            }
        }
        
        //para buscar mas precision
        double startL0 = this.pL0;
        double startT = this.pT;
        double startG = this.pG;
        double startS = this.pS;
        for (double l0 = startL0 - 0.009; l0 <= startL0 + 0.009 && l0 <= topL0; l0 += 0.001) {
            for (double t = startT - 0.009; t <= startT + 0.009 && t <= topT; t += 0.001) {
                for (double g = startG - 0.009; g <= startG + 0.009 && g <= topG; g += 0.001) {
                    for (double s = startS - 0.009; s <= startS + 0.009 && s <= topS; s += 0.001) {
                        SSR = findSSR(l0, t, g, s, itemsActuales);
                        if (SSR < bestSSR) {
                            bestSSR = SSR;
                            bestL0 = l0;
                            bestT = t;
                            bestS = s;
                            bestG = g;
//                            this.pL0 = l0;
//                            this.pT = t;
//                            this.pG = g;
//                            this.pS = s;
                        }
                    }
                }
            }
        }
        
        Parametros parametros = new Parametros(bestL0,bestT, bestG, bestS);
        return parametros;
    }

    private double findSSR(double l0, double t, double g, double s, List<BKT.Item> itemsActuales) {
        double SSR = 0.0;
        String estudianteAnterior = items.get(0).getEstudiante();
        double prevL = 0.0;
        double likelihoodCorrect = 0.0;
        double prevLGivenResult = 0.0;
        double newL = 0.0;

        for (BKT.Item item : items) {
            if (!item.getEstudiante().equalsIgnoreCase(estudianteAnterior)) {
                prevL = l0;
                estudianteAnterior = item.getEstudiante();
            }
            if (isEstimacionLnMenos1()) {
                likelihoodCorrect = prevL;
            } else {
                likelihoodCorrect = prevL * (1.0 - s) + (1.0 - prevL) * g;
            }
            double respuestaActual = item.isCorrecto() ? 1.0 : 0.0;
            SSR += (respuestaActual - likelihoodCorrect) * (respuestaActual - likelihoodCorrect);
            if (item.isCorrecto()) {
                prevLGivenResult = (prevL * (1.0 - s)) / (prevL * (1 - s) + (1.0 - prevL) * g);
            } else {
                prevLGivenResult = (prevL * s) / (prevL * s + (1.0 - prevL) * (1.0 - g));
            }

            newL = prevLGivenResult + (1.0 - prevLGivenResult) * t;
            prevL = newL;
        }

        return SSR;
    }

    /**
     * Ordena los items primero por la habilidad y luego por los estudiantes.
     *
     * @param items
     */
    private void sortItems(List<BKT.Item> items) {
        Collections.sort(items, (BKT.Item i1, BKT.Item i2) -> {
            int valor = i1.getHabilidad().compareTo(i2.getHabilidad());
            if (valor == 0) {
                return i1.getEstudiante().compareTo(i2.getEstudiante());
            }
            return valor;
        });
    }

    private List<String> getHabilidades() {
        List<String> habilidades = items
                .stream()
                .map(i -> {
                    return i.getHabilidad();
                })
                .distinct()
                .collect(Collectors.toList());
        return habilidades;
    }

    private List<String> getEstudiantes(List<BKT.Item> itemsActuales) {
        return itemsActuales.stream()
                .map(i -> {
                    return i.getEstudiante();
                })
                .distinct()
                .collect(Collectors.toList());
    }
}
