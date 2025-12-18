package HooYah.Gateway.domain.vo;

import java.util.regex.Pattern;

public final class UriMatcher {

    private final Pattern pattern;

    /*
       prefix (input uri matcher Str) : "/user"
       마지막에 '/' 강제로 붙임
       (생성된) pattern : ^ /user/ ...

       input 값에 적용 : '?' 이후 값 제거,  마지막에 '/' 값 추가
       input ex : /user?name="", -> /user/ (matching true!)
    */
    public UriMatcher(String prefix) { // "/user" or "/user/"
        String regex = "^" + Pattern.quote(toUriPattern(prefix));
        this.pattern = Pattern.compile(regex);
    }

    public boolean isMatch(Uri uri) {
        if (uri == null || uri.getUri() == null) {
            return false;
        }

        return pattern.matcher(toUriPattern(uri.getUri())).find();
    }

    private String toUriPattern(String input) {
        int questionIndex = input.indexOf('?');
        if(questionIndex != -1)
            input = input.subSequence(0, questionIndex).toString();

        if(!input.endsWith("/"))
            input += "/";

        return input;
    }

}
