package team3.src.message.response;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Response {
    enum Type {END, DATA_IN, DATA_OUT, WAIT, ERROR}
    Type value();
}
