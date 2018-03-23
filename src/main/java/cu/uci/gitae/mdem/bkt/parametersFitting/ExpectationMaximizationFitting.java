package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.BKT;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author angel
 */
public class ExpectationMaximizationFitting extends FittingMethodImpl {

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
    public Map<String, Parametros> fitParameters(List<BKT.Item> items) {

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
            List<BKT.Item> itemsHabilidad = items //Obtener los items para esa habilidad.
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
            Parametros param = new Parametros(); //hipotesis actual
            param.randomInit();
            double likelihood = 0.0;
            double menorErrorCuadrado = Double.MAX_VALUE; //parametro de control
            while (menorErrorCuadrado >= epsilon) {
                //Paso E: se estiman los datos faltantes en base a los parámetros actuales.
                /*2. Utilizar los datos conocidos con los parámetros actuales para 
                estimar los valores de la variable(s) oculta(s).*/
                double prevPL = param.getL0();
                double newPL; //probabilidad de dominar la habilidad
                double newPC; //probabilidad de responder correctamente
                for (BKT.Item item : itemsHabilidad) {
                    newPL = prevPL + param.getT() * (1.0 - prevPL); //calcula la probabilidad de dominar la habilidad 
                    newPC = param.getG() * (1.0 - prevPL) + (1.0 - param.getS()) * prevPL; //calcula la probabilidad de responder correctamente
                    if(item.isCorrecto()){
                        //calcular prevL cuando el item obtuvo respuesta correcta.
                        newPL = (prevPL*(1.0 - param.getS()))/(prevPL*(1.0-param.getS())+(1.0-prevPL)*param.getG());
                    }else{
                        //calcular prevL cuando el item obtuvo respuesta incorrecta
                        newPL = (prevPL*param.getS())/(prevPL*param.getS()+(1.0-prevPL)*(1.0 - param.getG()));
                    }
                    prevPL = newPL;
                    double correcto = (item.isCorrecto())?1.0:0.0;
                    //calcula el error cuadrático
                    likelihood += (correcto - newPC)*(correcto - newPC);
                }
                //Paso M: se estiman las probabilidades (parámetros) considerando los datos estimados.
                /*3. Utilizar los valores estimados para completar la tabla de datos.*/
                
                /*4. Re-estimar los parámetros con los nuevos datos*/
            }
        }

        /*Repetir 2-4 hasta que no haya cambios significativos en las 
        probabilidades.*/
        return null;
    }

    private double newPL(Parametros param, double prevPL) {
        double newPL = prevPL + param.getT() * (1.0 - prevPL);
        return newPL;
    }

}
