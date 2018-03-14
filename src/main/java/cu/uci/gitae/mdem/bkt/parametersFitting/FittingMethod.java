package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.BKT;
import java.util.List;

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
    public Double[] fitParameter(List<BKT.Item> items);
}
