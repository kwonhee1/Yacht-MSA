module Message.main {

    requires com.fasterxml.jackson.databind;
    requires spring.data.redis;

    requires static lombok;
    requires org.reactivestreams;
    requires java.logging;
    requires lettuce.core;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports HooYah.Yacht;
    exports HooYah.Yacht.subscriber;
    exports HooYah.Yacht.publisher;
    exports HooYah.Yacht.event;

}