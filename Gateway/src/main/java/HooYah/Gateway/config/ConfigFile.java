package HooYah.Gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Map;

/*
    ReadConfig file Class

        Usage example
    ConfigFile.SERVER_YML.getValue(Map.class) : get ServerYML as Map
    ConfigFile.APPLICATION_PROPERTIES.getValue(String.class, DB_PASSWORD) : get ApplicationProperty.DB_PASSWORD as String
 */
public enum ConfigFile {
    APPLICATION_PROPERTIES(FileDirect.ROOT, ".env", FileType.PROPERTIES),
    SERVER_YML(FileDirect.ROOT, "servers.yml",  FileType.YML),
    ;

    private Map content;
    private FileType fileType;

    ConfigFile(FileDirect fileDirect, String fileName, FileType fileType) {
        this.content = FileReader.readFile(fileDirect, fileName, fileType);
        this.fileType = fileType;
    }

    /*
        @Param clazz : value Class Type
        @Param direct : value direct
         if want "env.DB.PASSWORD" use "getValue(ConfigFile.env, String.class, "DB", "PASSWORD")"
    */
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

        private static Map readFile(FileDirect fileDirect, String fileName, FileType fileType) {
            try (InputStream inputStream = fileDirect.getInputStream(fileName)) {

                if (inputStream == null) {
                    throw new IllegalArgumentException("Resource not found: " + fileName);
                }

                // public <T> T readValue(File src, Class<T> valueType)
                if(fileType.equals(FileType.PROPERTIES))
                    return propertyMapper.readValue(inputStream, Map.class);
                else
                    return ymlMapper.readValue(inputStream, Map.class);
            } catch (IOException e) {
                throw new ReadFileException(fileName, e);
            }
        }
    }

    enum FileDirect {
        ROOT, // Root project, same level as src
        RESOURCE; // class path root, in resources forder

        private InputStream getInputStream(String fileName) {
            try {
                if (this.equals(FileDirect.ROOT))
                    return Files.newInputStream(Paths.get(fileName));
                // return new File(fileName).toURI().toURL().openStream();
                if (this.equals(FileDirect.RESOURCE))
                    return ApplicationConfig.class
                            .getClassLoader()
                            .getResourceAsStream(fileName);

            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new RuntimeException("Config.FileDirect :: getInputStream()");
        }
    }
}
