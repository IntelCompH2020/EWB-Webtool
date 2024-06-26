package gr.cite.intelcomp.evaluationworkbench.web.controllers;

import gr.cite.intelcomp.evaluationworkbench.web.model.QueryResult;
import gr.cite.intelcomp.evaluationworkbench.web.model.ValidationErrorResponse;
import gr.cite.tools.data.query.Lookup;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@ControllerAdvice
@Order(100)
public class BaseController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ValidationErrorResponse handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            try {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                if (errors.get(fieldName) != null) errors.get(fieldName).add(errorMessage);
                else errors.put(fieldName, Collections.singletonList(errorMessage));
            } catch (ClassCastException e) {
                String fieldName = error.getObjectName();
                String errorMessage = error.getDefaultMessage();
                if (errors.get(fieldName) != null) errors.get(fieldName).add(errorMessage);
                else errors.put(fieldName, Collections.singletonList(errorMessage));
            }
        });
        return new ValidationErrorResponse(errors);
    }

    public static <T, L extends Lookup> QueryResult<T> extractQueryResultWithCount(Function<L, List<T>> service, L lookup) {
        List<T> result = service.apply(lookup);
        lookup.setPage(null);
        int count = service.apply(lookup).size();
        return new QueryResult<>(result, count);
    }

    public static <T, L extends Lookup> QueryResult<T> extractQueryResultWithCount(BiFunction<String, L, List<T>> service, String name, L lookup) {
        List<T> result = service.apply(name, lookup);
        lookup.setPage(null);
        int count = service.apply(name, lookup).size();
        return new QueryResult<>(result, count);
    }

}
