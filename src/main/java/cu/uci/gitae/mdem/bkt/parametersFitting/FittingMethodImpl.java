package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.Item;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


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
    
    protected abstract Parametros ajustarModeloHabilidad(String habilidad);
}
