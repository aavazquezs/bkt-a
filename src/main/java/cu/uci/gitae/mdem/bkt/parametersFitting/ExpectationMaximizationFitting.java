package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.BKT;
import java.util.List;
import java.util.Map;

/**
 *
 * @author angel
 */
public class ExpectationMaximizationFitting extends FittingMethodImpl{

    /**
     * Método para estimar los parámetros de BKT mediante el metodo de 
     * maximización de la expectación.
     * Referencia: Sucar, L. E., & Tonantzintla, M. (2006). Redes Bayesianas. BS Araujo, 
     * Aprendizaje Automático: conceptos básicos y avanzados, 77-100.
     * @param items 
     * @return 
     */
    @Override
    public Map<String, Parametros> fitParameters(List<BKT.Item> items) {
        /* 1. Iniciar los parámetros desconocidos (probabilidades condicionales) 
        con valores aleatorios (o estimaciones de expertos)*/
        
        //Paso E: se estiman los datos faltantes en base a los parámetros actuales.
        /*2. Utilizar los datos conocidos con los parámetros actuales para 
        estimar los valores de la variable(s) oculta(s).*/

        //Paso M: se estiman las probabilidades (parámetros) considerando los datos
        //estimados.
        /*3. Utilizar los valores estimados para completar la tabla de datos.*/
        
        /*4. Re-estimar los parámetros con los nuevos datos*/
        
        /*Repetir 2-4 hasta que no haya cambios significativos en las 
        probabilidades.*/
        return null;
    }
    
}
