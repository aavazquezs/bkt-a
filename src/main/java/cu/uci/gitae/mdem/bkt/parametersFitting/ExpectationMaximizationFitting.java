package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 *
 * @author angel
 */
public class ExpectationMaximizationFitting extends FittingMethodImpl {

    private int maxIt;
    
    /**
     * Método para estimar los parámetros de BKT mediante el metodo de
     * maximización de la expectación. Se calcula el conjunto de parámetros para
     * cada habilidad Referencia: Sucar, L. E., & Tonantzintla, M. (2006). Redes
     * Bayesianas. BS Araujo, Aprendizaje Automático: conceptos básicos y
     * avanzados, 77-100.
     *
     * @param items
     * @return
     */
    @Override
    public Map<String, Parametros> fitParameters(List<Item> items) {
        final Double epsilon = 0.001; //parametro para controlar la convergencia
        Map<String, Parametros> resultado = new HashMap<>();
        List<String> habilidades = items //obtener el conjunto de habilidades
                .stream()
                .parallel()
                .map(item -> {
                    return item.getHabilidad();
                })
                .distinct()
                .collect(Collectors.toList());

        for (String habilidad : habilidades) { //Calcular parámetros para cada habilidad 
            List<Item> itemsHabilidad = items //Obtener los items para esa habilidad.
                    .stream().parallel()
                    .filter(item -> {
                        return item.getHabilidad().equalsIgnoreCase(habilidad);
                    })
                    .sorted((i1, i2) -> {
                        return i1.getEstudiante().compareToIgnoreCase(i2.getEstudiante());
                    })
                    .collect(Collectors.toList());
            /* 1. Iniciar los parámetros desconocidos (probabilidades condicionales) 
            con valores aleatorios (o estimaciones de expertos)*/
            
            List<Double> llhs = new ArrayList<>();
            Parametros param = new Parametros(); //hipotesis actual
            param.randomInit(); //inicializados con valores aleatorios
            double log_likelihood = 0.0, prevLog_likelihood= Double.MIN_VALUE, media;
            boolean converged = false;
            int count=0;
            double menorErrorCuadrado = Double.MAX_VALUE; //parametro de control
            while (!converged && count < this.maxIt) {
                //Paso E: se estiman los datos faltantes en base a los parámetros actuales.
                log_likelihood = this.EStep(param, itemsHabilidad);
                //adding the log likelihood
                llhs.add(log_likelihood);
                /*2. Utilizar los datos conocidos con los parámetros actuales para 
                estimar los valores de la variable(s) oculta(s).*/
                media = llhs.stream().collect(Collectors.averagingDouble(x->{return x;}));
                //Paso M: se estiman las probabilidades (parámetros) considerando los datos estimados.
                /*3. Utilizar los valores estimados para completar la tabla de datos.*/
                param = this.MStep(param, media);
                /*4. Re-estimar los parámetros con los nuevos datos*/
                converged = log_likelihood - prevLog_likelihood < epsilon;
                prevLog_likelihood = log_likelihood;
            }
            //
            resultado.put(habilidad, param);
        }
        
        /*Repetir 2-4 hasta que no haya cambios significativos en las 
        probabilidades.*/
        return resultado;
    }

    private Parametros MStep(Parametros param, Double media){
        Parametros nuevo = new Parametros();
        if(param.getL0()+media<=1){
            nuevo.setL0(param.getL0()+media);
        }else{
            nuevo.setL0(param.getL0()+media-1);
        }
        if(param.getT()+media<=1){
            nuevo.setT(param.getT()+media);
        }else{
            nuevo.setT(param.getT()+media -1 );
        }
        if(param.getG()+media <=1){
            nuevo.setG(param.getG()+media);
        }else{
            nuevo.setG(param.getG()+media-1);
        }
        if(param.getS()+media <=1){
            nuevo.setS(param.getS()+media);
        }else{
            nuevo.setS(param.getS()+media-1);
        }
        return nuevo;
    }
    
    /**
     * Calcula el likelihood de
     *
     * @param paramIniciales
     * @param items
     * @return
     */
    private double EStep(Parametros paramIniciales, List<Item> items) {
        double log_likelihood = 0.0;
        Parametros param = paramIniciales;
        double prevPL = param.getL0();
        double newPL; //probabilidad de dominar la habilidad
        double newPC; //probabilidad de responder correctamente
        for (Item item : items) {
            newPL = prevPL + param.getT() * (1.0 - prevPL); //calcula la probabilidad de dominar la habilidad 
            newPC = param.getG() * (1.0 - prevPL) + (1.0 - param.getS()) * prevPL; //calcula la probabilidad de responder correctamente
            if (item.isCorrecto()) {
                //calcular prevL cuando el item obtuvo respuesta correcta.
                newPL = (prevPL * (1.0 - param.getS())) / (prevPL * (1.0 - param.getS()) + (1.0 - prevPL) * param.getG());
            } else {
                //calcular prevL cuando el item obtuvo respuesta incorrecta
                newPL = (prevPL * param.getS()) / (prevPL * param.getS() + (1.0 - prevPL) * (1.0 - param.getG()));
            }
            prevPL = newPL;
            double correcto = (item.isCorrecto()) ? 1.0 : 0.0;
            //calcula el error cuadrático - TODO valorar quitar el error cuadrático
            log_likelihood += Math.log((correcto - newPC) * (correcto - newPC));
        }
        return log_likelihood;
    }

    @Override
    public Map<String, Parametros> fitParameters(Dataset<Item> dataset) {
        return null;
    }

    @Override
    protected Parametros ajustarModeloHabilidad(String habilidad) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Parametros> fitParameters2(Dataset<Row> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
