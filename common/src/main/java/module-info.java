module Yacht.main {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.net.http;

    exports HooYah.Yacht;
    exports HooYah.Yacht.webclient;
    exports HooYah.Yacht.excetion;
    exports HooYah.Yacht.webclient.response;
    exports HooYah.Yacht.webclient.send;
    exports HooYah.Yacht.webclient.config;
}