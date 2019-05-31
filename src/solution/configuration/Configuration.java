package solution.configuration;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Configuration {

    private ConfigModel configModel;
    private Gson gson;

    private static Configuration instance;

    private Configuration() {
        this.gson = new Gson();
        this.configModel = this.readConfig();
    }

    public static Configuration getInstance(){
        if (instance == null){
            instance = new Configuration();
        }
        return instance;
    }

    private ConfigModel readConfig(){
        StringBuilder sb = new StringBuilder();
        try {
            for (String s : Files.readAllLines(Paths.get("files/config.json"))){
                sb.append(s);
            }
        } catch (IOException e){
            System.err.println("Error reading the configuration file\nStackTrace:");
            e.printStackTrace();
        }
        return gson.fromJson(sb.toString(), ConfigModel.class);
    }

    public void saveConfig(){
        try (PrintWriter out = new PrintWriter("files/config.json")) {
            out.println(gson.toJson(configModel));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public ConfigModel getConfigModel() {
        return configModel;
    }
}
