package cu.uci.gitae.mdem.metrics;

import cu.uci.gitae.mdem.bkt.Item;
import cu.uci.gitae.mdem.bkt.parametersFitting.FittingMethod;
import cu.uci.gitae.mdem.bkt.parametersFitting.Parametros;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.Pair;

/**
 *
 * @author angel
 */
public class FittingMethodValidation {

    private List<Item> items;
    private List<String> estudiantes;
    private List<String> habilidades;
    private FittingMethod fittingMethod;

    public FittingMethodValidation(List<Item> items, FittingMethod fm) {
        this.items = items;
        estudiantes = this.items.stream().parallel()
                .map(i -> {
                    return i.getEstudiante();
                })
                .distinct()
                .collect(Collectors.toList());
        habilidades = this.items.stream().parallel()
                .map(i -> {
                    return i.getHabilidad();
                })
                .distinct()
                .collect(Collectors.toList());
        this.fittingMethod = fm;
    }

    public ROC obtenerCurvaRocGeneral() {
        Map<String, Parametros> params = this.fittingMethod.fitParameters(items);
        return null;//TODO
    }

    /**
     * Obtiene los registros del estudiante pasado por parametro ordenados por
     * la habilidad
     */
    private List<Item> getItemsEstudiante(String estudiante) {
        return items.stream().parallel()
                .filter(i -> {
                    return i.getEstudiante().equals(estudiante);
                })
                .sorted((i1, i2) -> {
                    return i1.getHabilidad().compareTo(i2.getHabilidad());
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el patron de respuestas para los item del estudiante
     *
     * @param itemsEstudiante
     * @return
     */
    private List<Double> getResponses(List<Item> itemsEstudiante) {
        return itemsEstudiante.stream().parallel()
                .map(i -> {
                    return i.isCorrecto() ? 1.0 : 0.0;
                })
                .collect(Collectors.toList());
    }
    /**
     * Obtiene los estimados de respuesta correcta segun el algoritmo BKT.
     * @param params
     * @param itemsEstudiante
     * @param P
     * @param N
     * @return 
     */
    private List<Double> getCorrectProbabilities(Map<String, Parametros> params, List<Item> itemsEstudiante) {
        String habPrev = "";
        List<Double> pC = new ArrayList<>();

        Parametros param;
        Double prevL, probL = 0.0;
        //calcular la pC una hab tras la otra.
        for (Item item : itemsEstudiante) {
            param = params.get(item.getHabilidad());
            if (!item.getHabilidad().equals(habPrev)) {
                prevL = param.getL0();
            } else {
                prevL = probL;
            }
            //calcula la probabilidad de dominio de la habilidad
            if (item.isCorrecto()) {
                probL = (prevL * (1.0 - param.getS())) / (prevL * (1.0 - param.getS() + (1.0 - prevL) * param.getG()));
            } else {
                probL = (prevL * param.getS()) / (prevL * param.getS() + (1. - 0 - prevL) * (1.0 - param.getG()));
            }
            //calcula y añade la probabilidad de responder correctamente
            pC.add(probL * (1.0 - param.getS() + (1.0 - probL) * param.getG()));
        }
        return pC;
    }

    public Map<String, Double> obtenerAPrimaPorEstudiante() {
        Map<String, Double> result = new HashMap<>();
        List<Item> itemsEstudiante;
        Map<String, Parametros> params = this.fittingMethod.fitParameters(items);
        for (String estudiante : estudiantes) {
            itemsEstudiante = this.getItemsEstudiante(estudiante);
            List<Double> responses = this.getResponses(itemsEstudiante);
            Long P = itemsEstudiante.stream().parallel()
                    .filter(Item::isCorrecto)
                    .count();
            Long N = itemsEstudiante.size() - P;
            if (P > 0 && N > 0) {
                List<Double> pC = this.getCorrectProbabilities(params, itemsEstudiante);
                ROC rocCurve = new ROC(responses, pC, P, N);
                Double aPrima = rocCurve.getAPrime();
                result.put(estudiante, aPrima);
            }else{
                result.put(estudiante, Double.NaN);
            }
        }
        return result;
    }

    /**
     * Calcula el AUC para cada estudiante, resultante de ajustar los parametros
     * usando el metodo de ajuste, guardado en la clase.
     *
     * @return
     * @throws Exception
     */
    public Map<String, Double> obtenerAucPorEstudiante() throws Exception {
        Map<String, Double> result = new HashMap<>();
        List<Item> itemsEstudiante;
        /*
        Ajustar los parametros para todas las habilidades
         */
        Map<String, Parametros> params = this.fittingMethod.fitParameters(items);

        /*
        Para cada estudiante calcular el área bajo la curva ROC
         */
        for (String estudiante : estudiantes) {
//            System.out.print("[Estudiante: " + estudiante + ", ");
            /*
            Obtener los registros del estudiante actual ordenados por la habilidad
             */
            itemsEstudiante = this.getItemsEstudiante(estudiante);
            /*
            Obtener el patron de respuestas
             */
            List<Double> responses = this.getResponses(itemsEstudiante);
            /*
            Contar la cantidad de respuestas correctas (P) e incorrectas (N)
             */

            Long P = itemsEstudiante.stream().parallel()
                    .filter(Item::isCorrecto)
                    .count();
            Long N = itemsEstudiante.size() - P;

            if (P > 0 && N > 0) {
                /*
                Obtener los estimados de respuesta correcta
                 */
                List<Double> pC = this.getCorrectProbabilities(params, itemsEstudiante);
                /*
                Crear la curva ROC y calcular el AUC
                 */
                ROC rocCurve = new ROC(responses, pC, P, N);
                Double auc = rocCurve.getAUC();
//                List<Pair<Double, Double>> points = rocCurve.getRocCurve();
//                rocCurve.exportRocCurveToFile(points, "./out/" + estudiante + "_ROC_curve.png", 500, 500);
//                rocCurve.exportAucToFile(auc, points, "./out/" + estudiante + "_AUC.png", 500, 500);
                result.put(estudiante, auc);

//                System.out.println(" AUC: " + auc + "]");
            } else {
                result.put(estudiante, Double.NaN);
//                System.out.println(" AUC: " + Double.NaN + "]");
            }
        }
        return result;
    }

    public Map<String, Double> obtenerAucPorEstudiante(FittingMethod fm) throws Exception {
        this.fittingMethod = fm;
        return this.obtenerAucPorEstudiante();
    }
}
