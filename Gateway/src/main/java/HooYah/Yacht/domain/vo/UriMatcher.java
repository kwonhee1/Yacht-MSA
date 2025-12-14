package HooYah.Yacht.domain.vo;

import java.util.regex.Pattern;
import org.springframework.http.server.PathContainer;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class UriMatcher {
    private static final PathPatternParser parser = new PathPatternParser();

    private final PathPattern pathPattern;

    public UriMatcher(String pattern) { // ex /user/**
        this.pathPattern = parser.parse(pattern);
    }

    public boolean isMatch(Uri uri) {
        if (uri == null) return false;

        return pathPattern.matches(PathContainer.parsePath(uri.getUri()));
    }
}
