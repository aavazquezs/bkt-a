package cu.uci.gitae.mdem.utils;

import java.util.Arrays;

/**
 * Algoritmo para ajustar parametros basado en el artículo de William J Hawkins
 * en https://sites.google.com/site/whawkins90/publications/ep
 * @author angel
 */
public class AnnotatingKnowledge {
    private Integer[] studentResponses;
    private Double[] k;
    private Double[] accuracy;

    /**
     * 
     * @param studentResponses 
     */
    public AnnotatingKnowledge(Integer[] studentResponses) {
        this.studentResponses = studentResponses;
        int len = studentResponses.length;
        accuracy = new Double[len+1];
        Arrays.fill(accuracy, 0.0);
        k = new Double[len];
        Arrays.fill(k, 0.0);
        int cont;
        Double bestAccuracy = Double.MIN_VALUE;
        for (int i = 0; i < len+1; i++) {
            cont = 0;
            for (int j = 0; j < len; j++) {
                if(this.studentResponses[j]==1 && j >= len - i){
                    cont++;
                }else if(this.studentResponses[j]==0 && j < len -i){
                    cont++;
                }
            }
            accuracy[i] = cont*1.0/len;
            if(accuracy[i] > bestAccuracy){
                bestAccuracy = accuracy[i];
            }
        }
        cont = 0;
        for (int i = 0; i < len+1; i++) {
            if(accuracy[i].equals(bestAccuracy)){
                cont++;
                for (int j = 0; j < len; j++) {
                    if(j < len - i){
                        k[j] += 0.0;
                    } else{
                        k[j] += 1.0;
                    }
                }
            }
        }
        for (int i = 0; i < len; i++) {
            k[i] = k[i]/cont;
        }
    }
    
    public Double[] getK() {
        return k;
    }
}
