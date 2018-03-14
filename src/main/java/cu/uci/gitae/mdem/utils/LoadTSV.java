package cu.uci.gitae.mdem.utils;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Clase de utilidad para leer un archivo en formato TSV (texto separado por tabs)
 * @author angel
 */
public class LoadTSV {
    /**
     * Retorna los token de cada fila como un arreglo de String
     * @param pathToFile Direccion al archivo donde esta el TSV
     * @return una lista donde cada elemento representa una fila, almacenando los elementos de ella en una arreglo de String
     * @throws FileNotFoundException 
     */
    public static List<String[]> loadTSV(String pathToFile) throws FileNotFoundException{
        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        // creates a TSV parser
        TsvParser parser = new TsvParser(settings);
        List<String[]> allRows = parser.parseAll(new FileInputStream(pathToFile));
        return allRows;
    }
    /**
     * Retorna los token de cada fila como un arreglo de String
     * @param pathToFile Direccion al archivo donde esta el TSV
     * @param columns Las columnas que se desea recuperar
     * @return una lista donde cada elemento representa una fila, almacenando los elementos de ella en una arreglo de String
     * @throws FileNotFoundException 
     */
    public static List<String[]> loadTSV(String pathToFile, String[] columns) throws FileNotFoundException{
        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.selectFields(columns);
        // creates a TSV parser
        TsvParser parser = new TsvParser(settings);
        List<String[]> allRows = parser.parseAll(new FileInputStream(pathToFile));
        return allRows;
    }
    /**
     * Retorna los token de cada fila como un arreglo de String
     * @param pathToFile Direccion al archivo donde esta el TSV
     * @param columns Indices de las columnas que se desea recuperar
     * @return una lista donde cada elemento representa una fila, almacenando los elementos de ella en una arreglo de String
     * @throws FileNotFoundException 
     */
    public static List<String[]> loadTSV(String pathToFile, Integer[] columns) throws FileNotFoundException{
        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.selectIndexes(columns);
        // creates a TSV parser
        TsvParser parser = new TsvParser(settings);
        List<String[]> allRows = parser.parseAll(new FileInputStream(pathToFile));
        return allRows;
    }
}
