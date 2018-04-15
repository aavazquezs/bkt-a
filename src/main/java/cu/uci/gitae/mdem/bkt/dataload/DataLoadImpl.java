package cu.uci.gitae.mdem.bkt.dataload;

import java.util.Map;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 *
 * @author angel
 */
public class DataLoadImpl implements DataLoad {

    SparkSession sparkSession;
    Dataset<Row> dataset;

    public DataLoadImpl(SparkSession sparkSession, Dataset<Row> dataset) {
        this.sparkSession = sparkSession;
        this.dataset = dataset;
    }

    public DataLoadImpl(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
        this.dataset = null;
    }

    public DataLoadImpl(String masterConfig) {
        sparkSession = SparkSession //crea la sesion de spark, se debe especificar el tipo de master
                .builder()
                .appName("BKT-A")
                .config("master", masterConfig)
                .getOrCreate();
    }

    //getters and setters
    public SparkSession getSparkSession() {
        return sparkSession;
    }

    public void setSparkSession(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
    }

    public Dataset<Row> getDataset() {
        return dataset;
    }

    public void setDataset(Dataset<Row> dataset) {
        this.dataset = dataset;
    }

    @Override
    public Dataset<Row> loadData(DataSourceType type, Map<String, String> parametros) {
        String datasetPath;
        switch (type) {
            case CSV:
                datasetPath = parametros.get("datasetPath");
                dataset = sparkSession.read() //lee el dataset desde un csv delimitado por tabs
                        .format("com.databricks.spark.csv")
                        .option("header", "true")
                        .load(datasetPath);
                break;
            case TSV:
                datasetPath = parametros.get("datasetPath");
                dataset = sparkSession.read() //lee el dataset desde un csv delimitado por tabs
                        .format("com.databricks.spark.csv")
                        .option("delimiter", "\t")
                        .option("header", "true")
                        .load(datasetPath);
                break;
            case Hive:
            case Casandra:
                this.dataset = null; //TODO 
                break;
        }
        return this.dataset;
    }

}
