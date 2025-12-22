package HooYah.Gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public enum ConfigFile {
    APPLICATION_PROPERTIES("application.properties", FileType.PROPERTIES),
    SERVER_YML("servers.yml",  FileType.YML),
    ;

    private Map content;
    private FileType fileType;

    ConfigFile(String fileDirect, FileType fileType) {
        this.content = FileReader.readFile(fileDirect, fileType);
        this.fileType = fileType;
    }

    public <T> T getValue(Class clazz, String... direct) {
        Object content = this.content;
        for(String dir : direct) {
            content = ((Map) content).get(dir);
        }

        if (this.fileType == FileType.PROPERTIES)
            return (T) FileReader.propertyMapper.convertValue(content, clazz);
        else
            return (T) FileReader.ymlMapper.convertValue(content, clazz);
    }

    class FileReader {
        final static ObjectMapper propertyMapper = new ObjectMapper(new JavaPropsFactory());
        final static ObjectMapper ymlMapper = new ObjectMapper(new YAMLFactory());

        private static Map readFile(String fileDirect,FileType fileType) {
            try (InputStream inputStream = ApplicationConfig.class
                    .getClassLoader()
                    .getResourceAsStream(fileDirect)) {

                if (inputStream == null) {
                    throw new IllegalArgumentException("Resource not found: " + fileDirect);
                }

                if(fileType.equals(FileType.PROPERTIES))
                    return propertyMapper.readValue(inputStream, Map.class);
                else
                    return ymlMapper.readValue(inputStream, Map.class);
            } catch (IOException e) {
                throw new ReadFileException(fileDirect, e);
            }
        }
    }
}
