package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.Item;
import cu.uci.gitae.mdem.utils.HiddenMarkovModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.spark.sql.Dataset;

/**
 *
 * @author angel
 */
public class BaumWelchFitting extends FittingMethodImpl{

    private List<Item> items;
    
    /**
     * Ajuste de parámetros para la version no paralela. Usando el Algoritmo 
     * Baum-Welch con escalado logaritmico.
     * @param items
     * @return 
     */
    @Override
    public Map<String, Parametros> fitParameters(List<Item> items) {
        this.items = items;
        Map<String, Parametros> resultado = new HashMap<>();
        List<String> habilidades = this.getHabilidades(this.items);
        habilidades.forEach(h->{
            Parametros actual = this.ajustarModeloHabilidad(h);
            resultado.put(h, actual);
        });
        return resultado;
    }

    /**
     * Ajuste de parámetros para la versión paralela con Spark;
     * @param dataset
     * @return 
     */
    @Override
    public Map<String, Parametros> fitParameters(Dataset<Item> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected Parametros ajustarModeloHabilidad(String habilidad){
        List<Item> itemsActuales = items
                .stream().parallel()
                .filter(t -> {
                    return t.getHabilidad().equals(habilidad);
                })
                .collect(Collectors.toList());
        Parametros param = new Parametros();
        param.randomInit();
        //pi[0] -> known
        //pi[1] -> unknown
        Double[] pi = new Double[]{param.getL0(), 1 - param.getL0()};
        Double[][] A = new Double[][]{
            {1.0, 0.0},
            {param.getT(), 1 - param.getT()}
        };
        Double[][] B = new Double[][]{
            {1 - param.getS(), param.getS()},
            {param.getG(), 1 - param.getG()}
        };
        
        HiddenMarkovModel hmm = new HiddenMarkovModel(2, 2, A, B, pi);
        int[] obs = itemsActuales.stream().parallel()
                .map(i->{
                    return i.isCorrecto()?1:0;
                })
                .mapToInt(Integer::intValue)
                .toArray();
        //entrenar 
        hmm.algorithmBaumWelchLog(obs, 0.001, 100);//TODO ver como ajustar estos parametros.
        Parametros resultado = new Parametros();
        resultado.setL0(hmm.getPi()[0]);
        resultado.setT(hmm.getA()[1][0]);
        resultado.setG(hmm.getB()[1][0]);
        resultado.setS(hmm.getB()[0][1]);
        return resultado;
    }
    
}
