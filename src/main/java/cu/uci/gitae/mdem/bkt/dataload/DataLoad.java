package cu.uci.gitae.mdem.bkt.dataload;

import java.util.Map;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 *
 * @author angel
 */
public interface DataLoad {
    /**
     * Método para cargar el dataset dado la fuente de datos y el conjunto de 
     * parámetros necesarios para cargarlos con esa fuente de datos.
     * @param type tipo de datasource
     * @param parametros parametros necesarios para cargar los datos de esa fuente
     * de datos
     * @return el conjunto de datos cargados.
     */
    public Dataset<Row> loadData(DataSourceType type, Map<String, String> parametros);
}
