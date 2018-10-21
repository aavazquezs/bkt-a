package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.BKT;
import cu.uci.gitae.mdem.bkt.Item;
import java.util.List;
import java.util.Map;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Interface que establece el contrato para los métodos de ajuste de parámetros
 * del algoritmo BKT.
 * @author angel
 */
public interface FittingMethod {
    /**
     * Retorna un arreglo con los parámetros del algoritmo ajustados según los 
     * datos.
     * @param items Conjunto de item actuales
     * @return arreglo con los parámetros ajustados.
     */
    public Map<String, Parametros> fitParameters(List<Item> items);
    /**
     * Retorna un mapeo con los parámetros por estudiante del algoritmo ajustados según los 
     * datos.
     * @param dataset Conjunto de datos con los items.
     * @return arreglo con los parámetros ajustados.
     */
    public Map<String, Parametros> fitParameters(Dataset<Item> dataset);
    
    public Map<String, Parametros> fitParameters2(Dataset<Row> dataset);
}
