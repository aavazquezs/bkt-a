package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.Item;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.apache.spark.sql.Dataset;

/**
 * Ajuste de parametro basado en el algoritmo Simulated Annealing Miller, W. L.,
 * Baker, R.S. and Rossi, L.M. (2014) [Unifying Computer-Based Assessment Across
 * Conceptual Instruction, Problem-Solving, and Digital Games]
 * (http://dx.doi.org/10.1007/s10758-014-9225-5). _Technology, Knowledge and
 * Learning_. 165-181.
 *
 * @author angel
 */
public class SimulatedAnnealingFitting extends FittingMethodImpl {

    List<Item> items;

    private final boolean bounded = true;
    private final boolean L0Tbounded = false;
    private final double stepSize = 0.05;
    private final double minVal = 0.000001;
    private final Integer totalSteps = 1000000;
    private Map<String, Double> top = new HashMap<>();

    @Override
    public Map<String, Parametros> fitParameters(List<Item> items) {
        this.items = items;
        Map<String, Parametros> resultado = new HashMap<>();
        this.sortItems(this.items);
        List<String> habilidades = this.getHabilidades(items);
        habilidades.forEach(h -> {
            Parametros actual = this.ajustarModeloHabilidad(h);
            resultado.put(h, actual);
        });
        return resultado;
    }

    @Override
    public Map<String, Parametros> fitParameters(Dataset<Item> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Parametros ajustarModeloHabilidad(String habilidad) {
        //Selecciona los items de la habilidad actual
        List<Item> itemsActuales = items.stream().parallel()
                .filter(i -> {
                    return i.getHabilidad().equals(habilidad);
                })
                .collect(Collectors.toList());

        if (L0Tbounded) {
            top.put("L0", 0.85);
            top.put("T", 0.3);
        } else {
            top.put("L0", 0.999999);
            top.put("T", 0.999999);
        }
        if (bounded) {
            top.put("G", 0.3);
            top.put("S", 0.1);
        } else {
            top.put("G", 0.999999);
            top.put("S", 0.999999);
        }

        // oldParams is randomized.
        Parametros oldParams = new Parametros();
        oldParams.randomInit();
        Parametros bestParams = new Parametros(0.01, 0.01, 0.01, 0.01);

        double oldRMSE = 1.0;
        double newRMSE = 1.0;
        double bestRMSE = Double.MAX_VALUE;
        double prevBestRMSE = Double.MAX_VALUE;
        double temp = 0.05;
        // Get the initial RMSE.
        oldRMSE = findGOOF(itemsActuales, oldParams);
        for (int i = 0; i < totalSteps; i++) {
            // Take a random step
            Parametros newParams = this.randStep(oldParams);
            newRMSE = findGOOF(itemsActuales, newParams);

            if (Math.random() <= Math.exp((oldRMSE - newRMSE) / temp)) {// Accept (otherwise move is rejected)
                oldParams = new Parametros(newParams);
                oldRMSE = newRMSE;
            }
            if (newRMSE < bestRMSE) {
                bestParams = new Parametros(newParams);
                bestRMSE = newRMSE;
            }
            if (i % 10000 == 0 && i > 0) { // Every 10,000 steps, decrease the "temperature."
                if (bestRMSE == prevBestRMSE) { // If the best estimate didn't change, we're done.
                    break;	
                }
                prevBestRMSE = bestRMSE;
                temp = temp / 2.0;
            }
        }

        return bestParams;
    }

    private double findGOOF(List<Item> itemsActuales, Parametros param) {
        double SSR = 0.0;
        //get random studen name
        Random rand = new Random();
        int aleatoria = rand.nextInt(items.size());
        String prevStudent = items.get(aleatoria).getEstudiante();

        double prevL = 0.0, prevLgivenresult, likelihoodcorrect;

        Integer count = 0;

        for (Item item : itemsActuales) {
            if (!item.getEstudiante().equals(prevStudent)) {
                prevL = param.getL0();
                prevStudent = item.getEstudiante();
            }
            likelihoodcorrect = (prevL * (1.0 - param.getS())) + ((1.0 - prevL) * param.getG());

            double respuestaActual = item.isCorrecto() ? 1.0 : 0.0;
            SSR += (respuestaActual - likelihoodcorrect) * (respuestaActual - likelihoodcorrect);
            count++;
            if (item.isCorrecto()) {
                prevLgivenresult = prevL * (1.0 - param.getS()) / (prevL * (1.0 - param.getS()) + (1.0 - prevL) * param.getG());
            } else {
                prevLgivenresult = prevL * param.getS() / (prevL * param.getS() + (1.0 - prevL) * (1.0 - param.getG()));
            }
            prevL = prevLgivenresult + prevLgivenresult * param.getT();
        }
        if (count == 0) {
            return 0.0;
        } else {
            return Math.sqrt(SSR / count);
        }
    }

    private Parametros randStep(Parametros actual) {
        Parametros nuevo = new Parametros(actual);
        Double randomChance = Math.random();
        Double thisStep = 2. * (Math.random() - 0.5) * stepSize;
        if (randomChance <= 0.25) {
            Double pL0 = Math.max(Math.min(actual.getL0() + thisStep, top.get("L0")), minVal);
            nuevo.setL0(pL0);
        } else if (randomChance <= 0.5) {
            Double pT = Math.max(Math.min(actual.getT() + thisStep, top.get("T")), minVal);
            nuevo.setT(pT);
        } else if (randomChance <= 0.75) {
            Double pG = Math.max(Math.min(actual.getG() + thisStep, top.get("G")), minVal);
            nuevo.setG(pG);
        } else {
            Double pS = Math.max(Math.min(actual.getS() + thisStep, top.get("S")), minVal);
            nuevo.setS(pS);
        }
        return nuevo;
    }

}
