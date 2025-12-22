package HooYah.Gateway.config;

public class ReadFileException extends RuntimeException {
    public ReadFileException(String filePath, Exception e) {
        super("Exception on read file : "+filePath, e.getCause());
    }
}
