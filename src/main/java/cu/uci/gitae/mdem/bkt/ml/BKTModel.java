package cu.uci.gitae.mdem.bkt.ml;

import cu.uci.gitae.mdem.bkt.BKT;
import cu.uci.gitae.mdem.bkt.Item;
import cu.uci.gitae.mdem.bkt.Result;
import cu.uci.gitae.mdem.bkt.parametersFitting.Parametros;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.spark.ml.Model;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

/**
 *
 * @author angel
 */
public class BKTModel extends Model<BKTModel> {

    private Map<String, Map<String, Parametros>> parametrosPorEstudiante;
    private final String _uid;

    public BKTModel() {
        this.parametrosPorEstudiante = new HashMap<>();
        this._uid = PreprocessTransformer.class.getName() + "_" + UUID.randomUUID().toString();
    }

    public BKTModel setParametrosPorEstudiante(Map<String, Map<String, Parametros>> parametrosPorEstudiante) {
        this.parametrosPorEstudiante = parametrosPorEstudiante;
        return this;
    }

    @Override
    public BKTModel copy(ParamMap arg0) {
        BKTModel copied = new BKTModel().setParametrosPorEstudiante(parametrosPorEstudiante);
        return copied;
    }

    @Override
    public Dataset<Row> transform(Dataset<?> dataset) {
        Dataset<Row> dst = dataset.select("*");
        Dataset<Item> items = this.encodeDataset(dst);
        Map<String, Map<String, Double>> resultado = new HashMap<>();
        List<Result> listResult = new LinkedList<>();
        parametrosPorEstudiante.keySet()
                .stream()
                .parallel()
                .forEach(est -> {
                    Map<String, Parametros> hp = parametrosPorEstudiante.get(est);
                    Dataset<Item> itemsEstudiantes
                            = items.filter(items.col("estudiante").equalTo(est));
                    Map<String, Double> habilidades = new HashMap<>();
                    hp.keySet()
                            .stream()
                            .parallel()
                            .forEach(hab->{
                                Dataset<Item> itemsEstHab = itemsEstudiantes
                                        .filter(itemsEstudiantes.col("habilidad").equalTo(hab));
                                BKT algoritmo = new BKT(items, hp.get(hab));
                                Double prob = algoritmo.execute2();
                                habilidades.put(hab, prob);
                                listResult.add(new Result(est, hab, prob));
                            });
                    resultado.put(est, habilidades);
                    
                });
        Encoder<Result> resultEncoder = Encoders.bean(Result.class);
        Dataset<Result> dstResult = dataset.sparkSession()
                .createDataset(listResult, resultEncoder);
        return dstResult.select("*");
    }

    private Dataset<Item> encodeDataset(Dataset<Row> dataset) {
        Encoder<Item> itemEncoder = Encoders.bean(Item.class);
        Dataset<Item> items = dataset
                .map(row -> {
                    Item i = new Item();
                    i.setCorrecto(row.getBoolean(0));
                    i.setEstudiante(row.getString(1));
                    i.setProblem(row.getString(2));
                    i.setHabilidad(row.getString(3));
                    return i;
                }, itemEncoder);
        return items;
    }

    @Override
    public StructType transformSchema(StructType oldSchema) {
        StructField[] fields = new StructField[3];
        fields[0] = new StructField("estudiante", DataTypes.StringType, false, Metadata.empty());
        fields[1] = new StructField("habilidad", DataTypes.StringType, false, Metadata.empty());
        fields[2] = new StructField("probabilidad", DataTypes.DoubleType, false, Metadata.empty());
        StructType schema = DataTypes.createStructType(fields);
        return schema;
    }

    @Override
    public String uid() {
        return this._uid;
    }

}
