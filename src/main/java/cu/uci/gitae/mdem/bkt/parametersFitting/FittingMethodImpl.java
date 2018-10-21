package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.Item;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;


/**
 * Clase abstracta para el ajuste de los parametros.
 * @author angel
 */
public abstract class FittingMethodImpl implements FittingMethod {
    
    
    
    protected double likelihood(Parametros param, double prevL){
        return prevL*(1.0 - param.getS()) + (1.0 - prevL)*param.getG();
    }
    /**
     * Ordena los items primero por la habilidad y luego por los estudiantes.
     *
     * @param items
     */
    protected void sortItems(List<Item> items) {
        Collections.sort(items, (Item i1, Item i2) -> {
            int valor = i1.getHabilidad().compareTo(i2.getHabilidad());
            if (valor == 0) {
                return i1.getEstudiante().compareTo(i2.getEstudiante());
            }
            return valor;
        });
    }
    
    /**
     * Obtener las habilidades del dataset
     * @param items
     * @return 
     */
    protected List<String> getHabilidades(List<Item> items) {
        List<String> habilidades = items
                .stream().parallel()
                .map(i -> {
                    return i.getHabilidad();
                })
                .distinct()
                .collect(Collectors.toList());
        return habilidades;
    }
    
    protected List<String> getHabilidades(Dataset<Item> items){
        List<String> habilidades = items
                .select("habilidad")
                .distinct()
                .map((row) -> {
                    return row.getAs("habilidad");
                }, Encoders.STRING())
                .collectAsList();
        return habilidades;
    }
    
    protected List<String> getHabilidades2(Dataset<Row> dataset){
        List<String> habilidades = dataset
                .select("habilidad")
                .distinct()
                .map((row) -> {
                    return row.getAs("habilidad");
                }, Encoders.STRING())
                .collectAsList();
        return habilidades;
    }
    /**
     * Obtener los estudiantes del listado de items
     * @param items
     * @return 
     */
    protected List<String> getEstudiantes(List<Item> items){
        List<String> estudiantes = items
                .stream().parallel()
                .map(i->{
                    return i.getEstudiante();
                })
                .distinct()
                .collect(Collectors.toList());
        return estudiantes;
    }
    
    protected List<String> getEstudiantes(Dataset<Item> items){
        List<String> estudiantes = items
                .select("estudiante")
                .distinct()
                .map((row) -> {
                    return row.getAs("estudiante");
                }, Encoders.STRING())
                .collectAsList();
        return estudiantes;
    }
    
    protected abstract Parametros ajustarModeloHabilidad(String habilidad);
}
