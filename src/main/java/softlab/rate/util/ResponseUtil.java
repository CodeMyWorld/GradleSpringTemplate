package softlab.rate.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Created by alex on 17-1-17.
 */

@Component("responseUtil")
public class ResponseUtil {

    private final String SUCCESS_CODE = "200";

    public <T> ResponseEntity<SuccessResponseBody<T>> getSuccessResponseEntity(T data){
        SuccessResponseBody<T> responseBody = new SuccessResponseBody<>();
        responseBody.setCode(SUCCESS_CODE);
        responseBody.setData(data);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    public ResponseEntity<ErrorResponseBody> getErrorResponseEntity(String errorMsg, String code){
        ErrorResponseBody errorResponseBody = new ErrorResponseBody();
        errorResponseBody.setCode(code);
        errorResponseBody.setMessage(errorMsg);
        return new ResponseEntity<>(errorResponseBody, HttpStatus.OK);
    }


    private class SuccessResponseBody<T>{
        private String code;
        private T data;

        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    private class ErrorResponseBody{
        private String code;
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
