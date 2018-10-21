package cu.uci.gitae.mdem.bkt.ml;

import cu.uci.gitae.mdem.bkt.parametersFitting.Parametros;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.spark.ml.Model;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
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
    public Dataset<Row> transform(Dataset<?> arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StructType transformSchema(StructType arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String uid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
