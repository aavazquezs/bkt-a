package cu.uci.gitae.mdem.bkt.ml;

import cu.uci.gitae.mdem.bkt.Item;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.spark.ml.Transformer;
import org.apache.spark.ml.param.Param;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.param.StringArrayParam;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

/**
 *
 * @author angel
 */
public class PreprocessTransformer extends Transformer implements Serializable {

//    private StringArrayParam _inputCols;
//    private StringArrayParam _outputCols;
    private final String _uid;
    private Map<String, String> param;

    public PreprocessTransformer() {
        this._uid = PreprocessTransformer.class.getName() + "_" + UUID.randomUUID().toString();
        this.param = new HashMap<>();
    }

    public PreprocessTransformer(String _uid) {
        this._uid = _uid;
    }

    //getters
//    public String[] getInputCols() {
//        return get(_inputCols).get();
//    }
//
//    public String[] getOutputCols() {
//        return get(_inputCols).get();
//    }
//
//    //setters
//    public PreprocessTransformer setInputCols(String[] columns) {
//        _inputCols = inputCols();
//        set(_inputCols, columns);
//        return this;
//    }
//
//    public StringArrayParam inputCols() {
//        return new StringArrayParam(this, "inputCols", "Columns to be preprocessed togother");
//    }
//
//    public PreprocessTransformer setInputCols(List<String> columns) {
//        String[] columnsString = columns.toArray(new String[columns.size()]);
//        return setInputCols(columnsString);
//    }
    @Override
    public Dataset<Row> transform(Dataset<?> dataset) {
        Dataset<Row> nuevo = null;
        param = new HashMap<>();
        //seleccionar solamente los campos observacion, estudiante_id, problema, habilidad
        nuevo = dataset.select("First Attempt", "Anon Student Id", "Problem", "KC (Original)");
        //eliminar cabecera - por la forma de cargar ya viene sin cabecera
        //eliminación de tuplas con campos faltantes
        String tokenEmpty;
        if (param.containsKey("emptySymbol")) {
            tokenEmpty = param.get("emptySymbol");
        } else {
            tokenEmpty = "";
        }
        nuevo = nuevo
                .filter(nuevo.col("First Attempt").notEqual(tokenEmpty))
                .filter(nuevo.col("Anon Student Id").notEqual(tokenEmpty))
                .filter(nuevo.col("Problem").notEqual(tokenEmpty))
                .filter(nuevo.col("KC (Original)").notEqual(tokenEmpty));
        //convertir el dataset de row al tipo item
        /*Encoder<Item> itemEncoder = Encoders.bean(Item.class);
        Dataset<Item> items = nuevo
                .map(row -> {
                    Item i = new Item();
                    i.setCorrecto(row.getString(0).equalsIgnoreCase("1"));
                    i.setEstudiante(row.getString(1));
                    i.setProblem(row.getString(2));
                    i.setHabilidad(row.getString(3));
                    return i;
                }, itemEncoder);*/
        return nuevo;
    }

    @Override
    public Transformer copy(ParamMap pm) {
        PreprocessTransformer copied = new PreprocessTransformer();

        return copied;
    }

    @Override
    public StructType transformSchema(StructType oldSchema) {
        StructField[] fields = oldSchema.fields();
        List<StructField> newFields = new ArrayList<>();
        List<String> campos = Arrays.asList("First Attempt", "Anon Student Id", "Problem", "KC (Original)");
        for (StructField field : fields) {
            if (campos.contains(field.name())) {
                newFields.add(field);
            }
        }
        StructType schema = DataTypes.createStructType(newFields);
        return schema;
    }

    @Override
    public String uid() {
        return this._uid;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }

}
