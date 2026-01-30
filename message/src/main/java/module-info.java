module Message.main {

    requires com.fasterxml.jackson.databind;
    requires spring.data.redis;

    requires static lombok;
    requires org.reactivestreams;
    requires java.logging;

    exports HooYah.Yacht;
    exports HooYah.Yacht.subscriber;
    exports HooYah.Yacht.publisher;
    exports HooYah.Yacht.dto;

}