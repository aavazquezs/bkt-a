package cu.uci.gitae.mdem.bkt;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.collection.Map;
import scala.collection.Seq;

/**
 *
 * @author angel
 */
public class Item implements Serializable, Row{

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

    @Override
    public int size() {
        return 4;
    }

    @Override
    public int length() {
        return 4;
    }

    @Override
    public StructType schema() {
        StructField[] fields = new StructField[4];
        fields[0] = new StructField("estudiante", DataTypes.StringType, false, Metadata.empty());
        fields[1] = new StructField("problem", DataTypes.StringType, false, Metadata.empty());
        fields[2] = new StructField("correcto", DataTypes.BooleanType, false, Metadata.empty());
        fields[3] = new StructField("habilidad", DataTypes.StringType, false, Metadata.empty());
        StructType schema = DataTypes.createStructType(fields);
        return schema;
    }

    @Override
    public Object apply(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object get(int i) {
        Object result;
        switch(i){
            case 0:
                result = this.estudiante;
                break;
            case 1:
                result = this.problem;
                break;
            case 2:
                result = this.correcto;
                break;
            default:
                result = this.habilidad;
        }
        return result;
    }

    @Override
    public boolean isNullAt(int i) {
        boolean result;
        switch(i){
            case 0:
                result = this.estudiante==null;
                break;
            case 1:
                result = this.problem==null;
                break;
            case 2:
                result = false;
                break;
            default:
                result = this.habilidad==null;
        }
        return result;
    }

    @Override
    public boolean getBoolean(int i) {
        if(i==2){
            return this.correcto;
        }else{
            return false;
        }
    }

    @Override
    public byte getByte(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public short getShort(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getInt(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getLong(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getFloat(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getDouble(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getString(int i) {
        String result;
        switch(i){
            case 0:
                result = this.estudiante;
                break;
            case 1:
                result = this.problem;
                break;
            case 2:
                result = this.correcto?"true":"false";
                break;
            default:
                result = this.habilidad;
        }
        return result;
    }

    @Override
    public BigDecimal getDecimal(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Date getDate(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Timestamp getTimestamp(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> Seq<T> getSeq(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> List<T> getList(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <K, V> Map<K, V> getMap(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <K, V> java.util.Map<K, V> getJavaMap(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Row getStruct(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T getAs(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T getAs(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int fieldIndex(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> scala.collection.immutable.Map<String, T> getValuesMap(Seq<String> seq) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Row copy() {
        Item copy = new Item(this.estudiante, this.problem, this.correcto, this.habilidad);
        return copy;
    }

    @Override
    public boolean anyNull() {
        return (this.estudiante==null || this.problem==null || this.habilidad==null);
    }

    @Override
    public Seq<Object> toSeq() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String mkString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String mkString(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String mkString(String string, String string1, String string2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
