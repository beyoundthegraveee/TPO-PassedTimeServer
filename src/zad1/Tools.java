/**
 *
 *  @author Kurzau Kiryl S24911
 *
 */

package zad1;


import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Tools {
    public static Options createOptionsFromYaml(String fileName) {
        try (FileInputStream io = new FileInputStream(fileName)){
            Map<String, Object> load = new Yaml().load(io);
            String host = (String) load.get("host");
            int port = (int) load.get("port");
            boolean concurMode = (boolean) load.get("concurMode");
            boolean showSendRes = (boolean) load.get("showSendRes");
            Map<String, List<String>> clientsMap = (Map<String, List<String>>) load.get("clientsMap");
            return new Options(host,port,concurMode,showSendRes,clientsMap);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
