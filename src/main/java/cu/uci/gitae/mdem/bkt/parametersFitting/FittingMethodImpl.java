package cu.uci.gitae.mdem.bkt.parametersFitting;


/**
 * Clase abstracta para el ajuste de los parametros.
 * @author angel
 */
public abstract class FittingMethodImpl implements FittingMethod {
    protected double likelihood(Parametros param, double prevL){
        return prevL*(1.0 - param.getS()) + (1.0 - prevL)*param.getG();
    }
}
