package cu.uci.gitae.mdem.bkt;

import java.io.Serializable;

/**
 *
 * @author angel
 */
public class Result implements Serializable{
    private String estudiante;
    private String habilidad;
    private Double probabilidad;

    public Result() {
        this.estudiante = "";
        this.habilidad = "";
        this.probabilidad = 0.0;
    }

    public Result(String estudiante, String habilidad, Double probabilidad) {
        this.estudiante = estudiante;
        this.habilidad = habilidad;
        this.probabilidad = probabilidad;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public String getHabilidad() {
        return habilidad;
    }

    public void setHabilidad(String habilidad) {
        this.habilidad = habilidad;
    }

    public Double getProbabilidad() {
        return probabilidad;
    }

    public void setProbabilidad(Double probabilidad) {
        this.probabilidad = probabilidad;
    }
    
}
