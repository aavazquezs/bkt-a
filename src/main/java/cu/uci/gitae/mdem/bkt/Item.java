package cu.uci.gitae.mdem.bkt;

import java.io.Serializable;
import org.apache.spark.sql.Row;

/**
 *
 * @author angel
 */
public class Item implements Serializable{

    private String estudiante;
    private String problem;
    private boolean correcto;
    private String habilidad;

    public Item() {
        this.correcto = false;
        this.habilidad = "";
        this.estudiante = "";
        this.problem = "";
    }

    public Item(boolean correcto, String habilidad) {
        this.correcto = correcto;
        this.habilidad = habilidad;
        this.estudiante = "";
        this.problem = "";
    }

    public Item(String estudiante, String problem, boolean correcto, String habilidad) {
        this.estudiante = estudiante;
        this.problem = problem;
        this.correcto = correcto;
        this.habilidad = habilidad;
    }

    public boolean isCorrecto() {
        return correcto;
    }

    public void setCorrecto(boolean correcto) {
        this.correcto = correcto;
    }

    public String getHabilidad() {
        return habilidad;
    }

    public void setHabilidad(String habilidad) {
        this.habilidad = habilidad;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    @Override
    public String toString() {
        String item = "[ correcto: "+(this.correcto?"1":"0")+",\testudiante_id: "+this.estudiante
                +",\thabilidad: "+this.habilidad+",\tproblema: "+this.problem+"]";
        return item; //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
