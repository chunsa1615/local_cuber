package kr.co.cntt.scc.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by jslivane on 2016. 7. 2..
 *
 * https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
 * https://msdn.microsoft.com/en-us/library/azure/dd179357.aspx
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "InternalServerError") // 500
@Slf4j
public class InternalServerError extends RuntimeException {
    public InternalServerError(String message) {
        super(message);

        log.error(message);

    }

}
