package cu.uci.gitae.mdem.bkt;

import cu.uci.gitae.mdem.bkt.dataload.DataLoad;
import cu.uci.gitae.mdem.bkt.dataload.DataLoadImpl;
import cu.uci.gitae.mdem.bkt.dataload.DataSourceType;
import java.util.HashMap;
import java.util.Map;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * Clase que implementa el algoritmo BKT adaptado a datos masivos.
 * @author angel
 */
public class BKTA {
    String masterConfig;
    String datasetPath;
    Dataset<Row> dataset;
    DataLoad dataLoad;
    //SparkSession spark;

    public BKTA(String masterConfig, String datasetPath) {
        this.masterConfig = masterConfig;
        this.datasetPath = datasetPath;
        this.dataset = null;
        this.dataLoad = new DataLoadImpl("BKT-A", masterConfig);
    }
    
    public void showDatasetSchema(){
        dataset.printSchema();
    }
    
    /**
     * Cargar el dataset a partir de una fuente de datos determinada.
     * @param type Tipo de fuente de dato DataSourceType
     * @param param Parametros necesarios para el tipo de datos elegido
     * @return Conjunto de datos obtenido a partir de la fuente de datos
     */
    public Dataset<Row> getDataset(DataSourceType type, Map<String, String> param) {
        dataset = dataLoad.loadData(DataSourceType.TSV, param);
        return dataset;
    }
    
    public Dataset<Row> preProcessDataset(){
        return null;
    }
    
    public Dataset<Row> fitParameters(){
        return null;
    } 
    
    public Dataset<Row> executeInParallel(){
        return null;
    }
    
    public Dataset<Row> getResults(){
        return null;
    }

    /**
     * Obtener el dataset actual
     * @return 
     */
    public Dataset<Row> getDataset() {
        return dataset;
    }
    
    
}
