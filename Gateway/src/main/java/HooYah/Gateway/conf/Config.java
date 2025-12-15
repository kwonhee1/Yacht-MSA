package HooYah.Gateway.conf;

import HooYah.Gateway.domain.module.Modules;
import HooYah.Gateway.domain.module.Module;
import HooYah.Gateway.domain.module.property.ModuleProperty;
import HooYah.Gateway.domain.server.Server;
import HooYah.Gateway.domain.server.property.ServerProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class Config {

    private YamlReader yamlReader = new YamlReader();

    @Bean
    public List<Server> servers() {
        List<ServerProperty> serverProperties = yamlReader.getValueList("servers", ServerProperty.class);
        List<Server> serverList = serverProperties.stream()
                .map(ServerProperty::toServer)
                .toList();
        return serverList;
    }

    @Bean
    public Modules modules() {
        List<ModuleProperty> moduleProperties = yamlReader.getValueList("modules", ModuleProperty.class);
        List<Module> moduleList = moduleProperties.stream()
                .map(f->f.toModule(servers()))
                .toList();
        return new Modules(moduleList);
    }

    class YamlReader {
        private static final String APPLICATION_YML = "servers.yml";

        private Map<String, String> ymlValue;
        private ObjectMapper jsonMapper = new ObjectMapper();

        public YamlReader() {
            try {
                init();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void init() throws IOException {
            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

            InputStream inputStream = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream(APPLICATION_YML);

            ymlValue = yamlMapper.readValue(inputStream, HashMap.class);
        }

        public <T> T getValue(String propertyPath, Class<T> clazz) {
            return jsonMapper.convertValue(ymlValue.get(propertyPath), clazz);
        }

        public <T> List<T> getValueList(String propertyPath, Class<T> clazz) {
            List<String> valueStr = getValue(propertyPath, List.class); // 이때 이미 List<Map> 형식으로 모두 변환이 되어있는 상태 --> 때문에 애당초 List<String>에 type 오류가 나야하지만 컴파일러가 잡지 못함 (runtime Exception 발생함
            List<T> result = new ArrayList<>();

            for(Object value : valueStr) {
                result.add(jsonMapper.convertValue(value, clazz)); // map to Object
            }

            return result;
        }
    }

}
