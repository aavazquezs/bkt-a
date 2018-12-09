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

    Dataset<Row> dataset;

    public DataLoadImpl() {
    }

//    public DataLoadImpl(String appName, String master) {
//    }
//
//    public DataLoadImpl(SparkSession sparkSession) {
//    }

    @Override
    public Dataset<Row> loadData(SparkSession sparkSession, DataSourceType type, Map<String, String> parametros) {
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
            case HDFS:
                datasetPath = parametros.get("datasetPath");
                //TODO
                this.dataset = null;
                break;
        }
        return this.dataset;
    }

}
