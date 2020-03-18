package kr.co.cntt.scc.util;

import kr.co.cntt.scc.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by jslivane on 2016. 6. 3..
 */
@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(UserDuplicatedException.class)
    public ResponseEntity handleUserDuplicatedException(UserDuplicatedException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("");
        errorResponse.setCode("");

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(Exception ex) {

        ModelAndView mav = new ModelAndView();
        mav.addObject("brand", Constants.BRAND_FULL);
        mav.addObject("title", "시스템 에러 발생");
        mav.addObject("exception", ex);
        mav.setViewName("error");

        return mav;
    }

}
