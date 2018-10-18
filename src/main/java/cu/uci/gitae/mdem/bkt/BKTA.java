package cu.uci.gitae.mdem.bkt;

import cu.uci.gitae.mdem.bkt.parametersFitting.EmpiricalProbabilitiesFitting;
import cu.uci.gitae.mdem.bkt.parametersFitting.FittingMethod;
import cu.uci.gitae.mdem.bkt.parametersFitting.Parametros;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;

/**
 * Clase que implementa el algoritmo BKT adaptado a datos masivos.
 *
 * @author angel
 */
public class BKTA implements Serializable{

    String masterConfig;
    String datasetPath;
    Dataset<Row> dataset;
    Dataset<Item> items;
    //DataLoad dataLoad;
    
    Dataset<Row> estudiantes;
    
    public BKTA(/*String masterConfig, String datasetPath*/) {
//        this.masterConfig = masterConfig;
//        this.datasetPath = datasetPath;
        this.dataset = null;
        //this.dataLoad = new DataLoadImpl();
    }
    
    public void setDataset(Dataset<Row> dataset) {
        this.dataset = dataset;
    }

    /**
     * Imprime el esquema del dataset.
     */
    public void showDatasetSchema() {
        dataset.printSchema();
    }

    /**
     * Cargar el dataset a partir de una fuente de datos determinada.
     *
     * @param type Tipo de fuente de dato DataSourceType
     * @param param Parametros necesarios para el tipo de datos elegido
     * @return Conjunto de datos obtenido a partir de la fuente de datos
     */
    /*
    public Dataset<Row> getDataset(DataSourceType type, Map<String, String> param) {
        SparkSession spark;
        SparkConf conf = new SparkConf().setAppName("BKT-A").setMaster(masterConfig);
        spark = SparkSession.builder()
                .config(conf)
                .getOrCreate();
        dataset = dataLoad.loadData(spark, DataSourceType.TSV, param);
        return dataset;
    }*/

    /**
     * Metodo para realizar el pre-procesamiento de los datos. Se realiza
     * selección de atributos. Eliminación de la cabecera del dataset
     * Eliminación de tuplas con datos faltantes.
     *
     * @param param parametros de configuración: emptySymbol indicando cual
     * simbolo será utilizado para representar el valor vacio, el valor por
     * defecto será null.
     * @return Conjunto de datos obtenido despues del pre-procesamiento
     * realizado
     */
    public Dataset<Item> preProcessDataset(Map<String, String> param) {
        //seleccion de atributos (observacion, estudiante_id, problema, habilidad)
        if (dataset.columns().length > 4) {
            //seleccionar solamente los campos observacion, estudiante_id, problema, habilidad
            List<String> fieldNames = Arrays.asList(dataset.schema().fieldNames());
            if (fieldNames.containsAll(Arrays.asList("First Attempt", "Anon Student Id", "Problem", "KC (Original)"))) {
                dataset = dataset.select("First Attempt", "Anon Student Id", "Problem", "KC (Original)");
            }
        }
        //eliminar cabecera - por la forma de cargar ya viene sin cabecera
        //eliminación de tuplas con campos faltantes
        String tokenEmpty;
        if (param.containsKey("emptySymbol")) {
            tokenEmpty = param.get("emptySymbol");
        } else {
            tokenEmpty = "";
        }
        dataset = dataset.filter(row -> {
            if (!row.anyNull()) {
                int size = row.size();
                for (int i = 0; i < size; i++) {
                    if (tokenEmpty.equalsIgnoreCase(row.getString(i))) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        });
        //convertir el dataset de row al tipo item
        Encoder<Item> itemEncoder = Encoders.bean(Item.class);
        Dataset<Item> items = dataset
                .map(row -> {
                    Item i = new Item();
                    i.setCorrecto(row.getString(0).equalsIgnoreCase("1"));
                    i.setEstudiante(row.getString(1));
                    i.setProblem(row.getString(2));
                    i.setHabilidad(row.getString(3));
                    return i;
                }, itemEncoder);
        this.items = items;
        
        this.estudiantes();
        
        return items;
    }

    /**
     * Metodo para realizar el ajuste de parámetros para el algoritmo en
     * paralelo
     *
     * @param items
     * @param param Parámetros para el método. El parámetro fittingMethod
     * permite determinar que método de ajuste utilizar, por defecto se
     * utilizará Expectation Maximization.
     * @return Map de parametros por habilidades para cada estudiante
     */
    public Map<String, Map<String, Parametros>> fitParameters(Dataset<Item> items, Map<String, String> param) {
        Map<String, Map<String, Parametros>> parametrosPorEstudiante
                = new HashMap<>();
        FittingMethod fm = new EmpiricalProbabilitiesFitting();
        
        this.estudiantes.collectAsList().forEach(row->{
            String estudianteActual = row.getString(0);
            Dataset<Item> actuales = items.filter(items.col("estudiante").equalTo(estudianteActual));
            Map<String, Parametros> ptem = fm.fitParameters(actuales);
            parametrosPorEstudiante.put(estudianteActual, ptem);
        });
        return parametrosPorEstudiante;
    }
    /**
     * Obtiene un dataset con los estudiantes 
     * @return 
     */
    private Dataset<Row> estudiantes(){
        this.estudiantes = this.items.select("estudiante").distinct();
        return this.estudiantes;
    }
    
    public Dataset<Row> executeInParallel() {
        
        return null;
    }

    public Dataset<Row> getResults() {
        return null;
    }

    /**
     * Obtener el dataset actual
     *
     * @return
     */
    public Dataset<Row> getDataset() {
        return dataset;
    }

}
