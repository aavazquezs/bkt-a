/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.uci.gitae.mdem.bkt.ml;

import cu.uci.gitae.mdem.bkt.Item;
import cu.uci.gitae.mdem.bkt.parametersFitting.EmpiricalProbabilitiesFitting;
import cu.uci.gitae.mdem.bkt.parametersFitting.FittingMethod;
import cu.uci.gitae.mdem.bkt.parametersFitting.Parametros;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.spark.ml.Estimator;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;

/**
 *
 * @author angel
 */
public class FitParameterEstimator extends Estimator<BKTModel> {

    private final String _uid;
    FittingMethod fm;
    

    public FitParameterEstimator() {
        this._uid = FitParameterEstimator.class.getName() + "_" + UUID.randomUUID().toString();
        fm = new EmpiricalProbabilitiesFitting();
    }
    
    public FitParameterEstimator setFittingMethod(FittingMethod fm){
        this.fm = fm;
        return this;
    }
    
    public FittingMethod getFittingMethod(){
        return this.fm;
    }

    @Override
    public BKTModel fit(Dataset<?> dataset) {
        Map<String, Map<String, Parametros>> parametrosPorEstudiante = new HashMap<>();
        Dataset<Row> estudiantes = dataset.select("estudiante").distinct();
        estudiantes
                .collectAsList()
                .forEach(row->{
            String estudianteActual = row.getString(0);
            Dataset<Item> dst = this.encodeDataset(dataset.select("*"));
            Dataset<Item> actuales = dst.filter(dst.col("estudiante").equalTo(estudianteActual));
            Map<String, Parametros> ptem = fm.fitParameters(actuales);
            parametrosPorEstudiante.put(estudianteActual, ptem);
        });
        BKTModel model = new BKTModel()
                .setParametrosPorEstudiante(parametrosPorEstudiante);
        return model;
    }

    private Dataset<Item> encodeDataset(Dataset<Row> dataset){
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
    public Estimator<BKTModel> copy(ParamMap arg0) {
        FitParameterEstimator copied = new FitParameterEstimator().setFittingMethod(fm);
        return copied;
    }

    @Override
    public StructType transformSchema(StructType oldSchema) {
        return new BKTModel().transformSchema(oldSchema);
    }

    @Override
    public String uid() {
        return this._uid;
    }

}
