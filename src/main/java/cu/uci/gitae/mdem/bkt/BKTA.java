package cu.uci.gitae.mdem.bkt;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * Clase que implementa el algoritmo BKT adaptado a datos masivos.
 * @author angel
 */
public class BKTA {
    Dataset<Row> dataset;
    SparkSession spark;

    public BKTA(String masterConfig, String datasetPath) {
        spark = SparkSession    //crea la sesion de spark, se debe especificar el tipo de master
                .builder()
                .appName("BKT-A")
                .config("master", masterConfig)
                .getOrCreate();
        dataset = spark.read()  //lee el dataset desde un csv delimitado por tabs
                .format("com.databricks.spark.csv")
                .option("delimiter", "\t")
                .option("header", "true")
                .load(datasetPath);
    }
    
    public void showDatasetSchema(){
        dataset.printSchema();
    }

    public Dataset<Row> getDataset() {
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
    
    
}
