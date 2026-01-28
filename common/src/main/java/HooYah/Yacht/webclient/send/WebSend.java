package HooYah.Yacht.webclient.send;

import HooYah.Yacht.webclient.WebClient.HttpMethod;

public interface WebSend {

    /*
        send web client sync

        @Param url : url (http://uri/path/...)
        @Parma method : Http Method (GET, POST, PUT, DELETE, PATCH)
        @Return String : json
     */
    String send(String url, HttpMethod method, Object body);

    /*
        send web client Async

        @Param url : url (http://uri/path/...)
        @Parma method : Http Method (GET, POST, PUT, DELETE, PATCH)
        @Return void : not yet! but must need!
    */
    void sendAsync(String url, HttpMethod method, Object body);

}
