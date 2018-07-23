package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.Item;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.spark.sql.Dataset;

/**
 *
 * @author angel
 */
public class RandomFitting extends FittingMethodImpl {

    private List<Item> items;
    
    @Override
    protected Parametros ajustarModeloHabilidad(String habilidad) {
        Parametros param = new Parametros();
        param.randomInit();
        return param;
    }

    @Override
    public Map<String, Parametros> fitParameters(List<Item> items) {
        this.items = items;
        Map<String, Parametros> resultado = new HashMap<>();
        //this.sortItems(this.items);
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
    
}
