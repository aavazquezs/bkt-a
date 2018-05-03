package cu.uci.gitae.mdem.bkt;

import cu.uci.gitae.mdem.bkt.dataload.DataLoad;
import cu.uci.gitae.mdem.bkt.dataload.DataLoadImpl;
import cu.uci.gitae.mdem.bkt.dataload.DataSourceType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * Clase que implementa el algoritmo BKT adaptado a datos masivos.
 * @author angel
 */
public class BKTA {
    //String masterConfig;
    //String datasetPath;
    Dataset<Row> dataset;
    DataLoad dataLoad;
    SparkSession spark;

    public BKTA(String masterConfig, String datasetPath) {
        //this.masterConfig = masterConfig;
        //this.datasetPath = datasetPath;
        this.dataset = null;
        SparkConf conf = new SparkConf().setAppName("BKT-A").setMaster(masterConfig);
        this.spark = SparkSession.builder()
                .config(conf)
                .getOrCreate();
        this.dataLoad = new DataLoadImpl(spark);
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
    /**
     * Metodo para realizar el pre-procesamiento de los datos. 
     * Se realiza selección de atributos. 
     * Eliminación de la cabecera del dataset
     * Eliminación de tuplas con datos faltantes.
     * @param param parametros de configuración: emptySymbol indicando cual 
     * simbolo será utilizado para representar el valor vacio, el valor por 
     * defecto será null.
     * @return Conjunto de datos obtenido despues del pre-procesamiento realizado
     */
    public Dataset<Row> preProcessDataset(Map<String, String> param){
        //seleccion de atributos (observacion, estudiante_id, problema, habilidad)
        if(dataset.columns().length>4){
            //seleccionar solamente los campos observacion, estudiante_id, problema, habilidad
            List<String> fieldNames = Arrays.asList(dataset.schema().fieldNames());
            if(fieldNames.containsAll(Arrays.asList("First Attempt","Anon Student Id","Problem","KC (Original)"))){
                dataset = dataset.select("First Attempt","Anon Student Id","Problem","KC (Original)");
            }
        }
        //eliminar cabecera - por la forma de cargar ya viene sin cabecera
        //eliminación de tuplas con campos faltantes
        String tokenEmpty;
        if(param.containsKey("emptySymbol")){
            tokenEmpty = param.get("emptySymbol");
        }else{
            tokenEmpty = "";
        }
        dataset = dataset.filter(row->{
            if(!row.anyNull()){
                int size = row.size();
                for (int i = 0; i < size; i++) {
                    if(tokenEmpty.equalsIgnoreCase(row.getString(i))){
                        return false;
                    }
                }
                return true;
            }else{
                return false;
            }
        });
        return dataset;
    }
    
    /**
     * Metodo para realizar el ajuste de parámetros para el algoritmo en paralelo
     * @param param
     * @return 
     */
    public Dataset<Row> fitParameters(Map<String, String> param){
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
