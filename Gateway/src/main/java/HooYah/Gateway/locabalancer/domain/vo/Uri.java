package HooYah.Gateway.locabalancer.domain.vo;

import java.net.URI;
import java.util.regex.Pattern;

public class Uri {

    private final String uri; // ex) /user
    private final Pattern pattern; // 매칭을 위한 패턴

    public Uri(String uri) {
        this.uri = uri;
        this.pattern = Pattern.compile("^" + Pattern.quote(toUriPattern(uri)));
    }

    public String getUri() {
        return uri;
    }

    public static Uri from(URI uri) {
        return new Uri(uri.getRawPath());
    }

    /*
       prefix (this uri) : "/user"
       마지막에 '/' 강제로 붙임
       (생성된) pattern : ^ /user/ ...
       
       input 값에 적용 : '?' 이후 값 제거,  마지막에 '/' 값 추가
       input ex : /user?name="", -> /user/ (matching true!)
    */
    public boolean isMatch(Uri requestUri) {
        if (requestUri == null || requestUri.getUri() == null) {
            return false;
        }

        return pattern.matcher(toUriPattern(requestUri.getUri())).find();
    }

    // /user/public/test -> /public/test 로 변환됨
    public String toProxyUri(Uri requestUri) {
        String proxyUri = pattern.matcher(requestUri.toString()).replaceFirst("");

        if(!proxyUri.startsWith("/"))
            proxyUri = "/" + proxyUri;

        // 마지막에 '/'을 붙여줌 (? url parameter가 없을 경우만
//        if( (!proxyUri.endsWith("/")) && (proxyUri.indexOf('?') == -1) )
//            proxyUri += "/";

        return proxyUri;
    }

    private String toUriPattern(String input) {
        int questionIndex = input.indexOf('?');
        if(questionIndex != -1)
            input = input.subSequence(0, questionIndex).toString();

        if(!input.endsWith("/"))
            input += "/";

        return input;
    }

    @Override
    public String toString() {
        return uri;
    }

}
