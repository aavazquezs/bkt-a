package cu.uci.gitae.mdem.bkt.dataload;

import java.io.Serializable;
import java.util.Map;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 *
 * @author angel
 */
public interface DataLoad extends Serializable{
    /**
     * Método para cargar el dataset dado la fuente de datos y el conjunto de 
     * parámetros necesarios para cargarlos con esa fuente de datos.
     * @param sparkSession
     * @param type tipo de datasource
     * @param parametros parametros necesarios para cargar los datos de esa fuente
     * de datos
     * @return el conjunto de datos cargados.
     */
    public Dataset<Row> loadData(SparkSession sparkSession, DataSourceType type, Map<String, String> parametros);
}
