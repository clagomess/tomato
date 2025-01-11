package com.github.clagomess.tomato.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanCollectionV210Dto {
    private Info info;
    private List<Item> item;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Info {
        @JsonProperty("_postman_id")
        private String id;
        private String name;
        private String schema = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json";
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String name;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<Item> item;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Request request;

        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Request {
            private Auth auth;
            private String method;

            @JsonInclude(JsonInclude.Include.NON_NULL)
            private Body body;
            private List<Header> header;
            private Url url;

            @Getter
            @Setter
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Auth {
                private String type;

                @JsonInclude(JsonInclude.Include.NON_NULL)
                private List<Bearer> bearer;

                @Getter
                @Setter
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class Bearer {
                    private String key;
                    private String value;
                    private String type;
                }
            }

            @Getter
            @Setter
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Body {
                private String mode;

                @JsonInclude(JsonInclude.Include.NON_NULL)
                private String raw;

                @JsonInclude(JsonInclude.Include.NON_NULL)
                private List<UrlEncoded> urlencoded;

                @JsonInclude(JsonInclude.Include.NON_NULL)
                private List<FormData> formdata;

                @JsonInclude(JsonInclude.Include.NON_NULL)
                private Options options;

                @Getter
                @Setter
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class UrlEncoded {
                    private String key;
                    private String value;
                    private String type;

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    private Boolean disabled;
                }

                @Getter
                @Setter
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class FormData {
                    private String key;
                    private String type;
                    private String src;
                    private String value;

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    private Boolean disabled;
                }

                @Getter
                @Setter
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class Options {
                    private Raw raw;

                    @Getter
                    @Setter
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    public static class Raw {
                        private String language;
                    }
                }
            }

            @Getter
            @Setter
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Header {
                private String key;
                private String value;

                @JsonInclude(JsonInclude.Include.NON_NULL)
                private Boolean disabled;
            }

            @Getter
            @Setter
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Url {
                private String raw;

                @JsonInclude(JsonInclude.Include.NON_NULL)
                private List<Variable> variable;

                @Getter
                @Setter
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class Variable {
                    private String key;
                    private String value;
                }
            }
        }
    }
}
