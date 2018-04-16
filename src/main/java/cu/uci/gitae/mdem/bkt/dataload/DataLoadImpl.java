package cu.uci.gitae.mdem.bkt.dataload;

import java.util.Map;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 *
 * @author angel
 */
public class DataLoadImpl implements DataLoad {

    JavaSparkContext jsc;
    SparkSession sparkSession;
    Dataset<Row> dataset;

    public DataLoadImpl(String appName, String master) {
        SparkConf conf = new SparkConf().setAppName(appName).setMaster(master);
        this.sparkSession = SparkSession.builder()
                .config(conf)
                .getOrCreate();
        this.jsc = new JavaSparkContext(sparkSession.sparkContext());
    }

    @Override
    public Dataset<Row> loadData(DataSourceType type, Map<String, String> parametros) {
        String datasetPath;
        switch (type) {
            case CSV:
                datasetPath = parametros.get("datasetPath");
                dataset = sparkSession.read()
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
            case JSON:
            case Hive:
            case Casandra:
                this.dataset = null; //TODO 
                break;
        }
        return this.dataset;
    }

}
