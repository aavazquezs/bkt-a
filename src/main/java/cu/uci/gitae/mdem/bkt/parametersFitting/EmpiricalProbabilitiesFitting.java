package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.Item;
import cu.uci.gitae.mdem.utils.AnnotatingKnowledge;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;

/**
 *
 * @author angel
 */
public class EmpiricalProbabilitiesFitting extends FittingMethodImpl implements Serializable {

    List<Item> items;
    Dataset<Item> dataset;

    
    @Override
    public Map<String, Parametros> fitParameters2(Dataset<Row> dataset) {
        Map<String, Parametros> resultado = new HashMap<>();
        List<String> habilidades = this.getHabilidades2(dataset);
        habilidades.forEach(h -> {
            Parametros actual = this.ajustarModeloHabilidad2(h);
            resultado.put(h, actual);
        });
        return resultado;
    }
    
    @Override
    public Map<String, Parametros> fitParameters(List<Item> items) {
        Map<String, Parametros> resultado = new HashMap<>();
        this.items = items;
        List<String> habilidades = this.getHabilidades(items);
        habilidades.forEach(h -> {
            Parametros actual = this.ajustarModeloHabilidad(h);
            resultado.put(h, actual);
        });
        return resultado;
    }

    @Override
    public Map<String, Parametros> fitParameters(Dataset<Item> dataset) {
        Map<String, Parametros> resultado = new HashMap<>();
        this.dataset = dataset;
        List<String> habilidades = this.getHabilidades(dataset);
        habilidades.forEach(h -> {
            Parametros actual = this.ajustarModeloHabilidad(h);
            resultado.put(h, actual);
        });
        return resultado;
    }

    /**
     * Método que obtiene los parametros ajustados para la habilidad dada.
     * Consta de dos pasos primero anotar el conocimiento y luego computar las
     * probabilidades.
     *
     * @param habilidad
     * @return
     */
    /*
    @Override
    protected Parametros ajustarModeloHabilidad(String habilidad) {
        Parametros resultado = new Parametros();
        List<Item> actuales = items.stream()
                .filter(i -> {
                    return i.getHabilidad().equalsIgnoreCase(habilidad);
                })
                .collect(toList());

        List<String> estudiantes = this.getEstudiantes(actuales);
        //Step 1 - Annotating Knowledge
        Map<String, Double[]> map = new HashMap<>();
        Map<String, Integer[]> respEst = new HashMap<>();
        estudiantes.forEach((String e) -> {
            Integer[] responses = actuales.stream().parallel()
                    .filter(i -> {
                        return i.getEstudiante().equalsIgnoreCase(e);
                    })
                    .map(i -> {
                        return i.isCorrecto() ? 1 : 0;
                    })
                    .toArray(Integer[]::new);
            respEst.put(e, responses);
            AnnotatingKnowledge ak = new AnnotatingKnowledge(responses);
            map.put(e, ak.getK());
        });
        //Step 2 - Computing the Probabilities
        double pL0 = 0.0;
        Double numT = 0.0, demT = 0.0, numG = 0.0, demG = 0.0, numS = 0.0, demS = 0.0;
        int cont = 0;
        for (Map.Entry<String, Double[]> entry : map.entrySet()) {
            String key = entry.getKey();
            Double[] value = entry.getValue();
            Integer[] resp = respEst.get(key);
            cont++;
            pL0 += value[0];

            for (int i = 1; i < value.length; i++) {
                numT += (1.0 - value[i - 1]) * value[0];
                demT += (1.0 - value[i - 1]);
            }

            for (int i = 0; i < value.length; i++) {
                numG += resp[i] * (1.0 - value[i]);
                demG += 1.0 - value[i];
                numS += (1.0 - resp[i]) * value[i];
                demS += value[i];
            }
        }
        pL0 = pL0 / cont;
        Double pT = numT / demT;
        Double pS = numS / demS;
        Double pG = numG / demG;
        resultado.setL0(pL0);
        resultado.setT(pT);
        resultado.setG(pG);
        resultado.setS(pS);
        return resultado;
    }
    */
    
    protected Parametros ajustarModeloHabilidad2(String habilidad){
        Parametros resultado = new Parametros();
        Dataset<Item> actuales = dataset
                .filter(dataset.col("habilidad").equalTo(habilidad));
        List<String> estudiantes = dataset
                .select("estudiante")
                .distinct()
                .map((row) -> {
                    return row.getAs("estudiante");
                }, Encoders.STRING())
                .collectAsList();
        //Step 1 - Annotating Knowledge
        Map<String, Double[]> map = new HashMap<>();
        Map<String, Integer[]> respEst = new HashMap<>();
        estudiantes.forEach((String e) -> {
            List<Integer> listResponses = actuales
                    .filter(i->{
                        return i.getEstudiante().equalsIgnoreCase(e);
                    })
                    .map(i->{
                        return i.isCorrecto() ? 1 : 0;
                    }, Encoders.INT())
                    .collectAsList();
            Integer[] responses = listResponses.stream().parallel().toArray(Integer[]::new);
            respEst.put(e, responses);
            AnnotatingKnowledge ak = new AnnotatingKnowledge(responses);
            map.put(e, ak.getK());
        });
        //Step 2 - Computing the Probabilities
        double pL0 = 0.0;
        Double numT = 0.0, demT = 0.0, numG = 0.0, demG = 0.0, numS = 0.0, demS = 0.0;
        int cont = 0;
        for (Map.Entry<String, Double[]> entry : map.entrySet()) {
            String key = entry.getKey();
            Double[] value = entry.getValue();
            Integer[] resp = respEst.get(key);
            cont++;
            pL0 += value[0];

            for (int i = 1; i < value.length; i++) {
                numT += (1.0 - value[i - 1]) * value[0];
                demT += (1.0 - value[i - 1]);
            }

            for (int i = 0; i < value.length; i++) {
                numG += resp[i] * (1.0 - value[i]);
                demG += 1.0 - value[i];
                numS += (1.0 - resp[i]) * value[i];
                demS += value[i];
            }
        }
        pL0 = pL0 / cont;
        Double pT = numT / demT;
        Double pS = numS / demS;
        Double pG = numG / demG;
        resultado.setL0(pL0);
        resultado.setT(pT);
        resultado.setG(pG);
        resultado.setS(pS);
        return resultado;
    }
    
    
    @Override
    protected Parametros ajustarModeloHabilidad(String habilidad) {
        Parametros resultado = new Parametros();
        Dataset<Item> actuales = dataset
                .filter(dataset.col("habilidad").equalTo(habilidad));
        List<String> estudiantes = this.getEstudiantes(actuales);
        //Step 1 - Annotating Knowledge
        Map<String, Double[]> map = new HashMap<>();
        Map<String, Integer[]> respEst = new HashMap<>();
        estudiantes.forEach((String e) -> {
            List<Integer> listResponses = actuales
                    .filter(i->{
                        return i.getEstudiante().equalsIgnoreCase(e);
                    })
                    .map(i->{
                        return i.isCorrecto() ? 1 : 0;
                    }, Encoders.INT())
                    .collectAsList();
            Integer[] responses = listResponses.stream().parallel().toArray(Integer[]::new);
            respEst.put(e, responses);
            AnnotatingKnowledge ak = new AnnotatingKnowledge(responses);
            map.put(e, ak.getK());
        });
        //Step 2 - Computing the Probabilities
        double pL0 = 0.0;
        Double numT = 0.0, demT = 0.0, numG = 0.0, demG = 0.0, numS = 0.0, demS = 0.0;
        int cont = 0;
        for (Map.Entry<String, Double[]> entry : map.entrySet()) {
            String key = entry.getKey();
            Double[] value = entry.getValue();
            Integer[] resp = respEst.get(key);
            cont++;
            pL0 += value[0];

            for (int i = 1; i < value.length; i++) {
                numT += (1.0 - value[i - 1]) * value[0];
                demT += (1.0 - value[i - 1]);
            }

            for (int i = 0; i < value.length; i++) {
                numG += resp[i] * (1.0 - value[i]);
                demG += 1.0 - value[i];
                numS += (1.0 - resp[i]) * value[i];
                demS += value[i];
            }
        }
        pL0 = pL0 / cont;
        Double pT = numT / demT;
        Double pS = numS / demS;
        Double pG = numG / demG;
        resultado.setL0(pL0);
        resultado.setT(pT);
        resultado.setG(pG);
        resultado.setS(pS);
        return resultado;
    }


}
