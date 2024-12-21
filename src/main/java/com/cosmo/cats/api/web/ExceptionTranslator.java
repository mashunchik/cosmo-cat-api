package com.cosmo.cats.api.web;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.cosmo.cats.api.service.exception.DuplicateProductNameException;
import com.cosmo.cats.api.service.exception.ProductAdvisorApiException;
import com.cosmo.cats.api.service.exception.ProductNotFoundException;
import com.cosmo.cats.api.util.InvalidatedParams;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ProductNotFoundException.class)
  public ProblemDetail handleProductNotFoundException(ProductNotFoundException ex) {
    return createProblemDetail(HttpStatus.NOT_FOUND, "Product not found", "product-not-found", ex.getMessage());
  }

  @ExceptionHandler(DuplicateProductNameException.class)
  public ProblemDetail handleDuplicateProductNameException(DuplicateProductNameException ex) {
    return createProblemDetail(HttpStatus.BAD_REQUEST, "Duplicate name", "this-name-exists", ex.getMessage());
  }

  @ExceptionHandler(ProductAdvisorApiException.class)
  public ProblemDetail handleProductAdvisorApiException(ProductAdvisorApiException ex) {
    return createProblemDetail(ex.getStatus(), "Could not get price advice", "price-advisor-error", ex.getMessage());
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
          MethodArgumentNotValidException ex,
          HttpHeaders headers, HttpStatusCode status,
          WebRequest request) {
    List<FieldError> errors = ex.getBindingResult().getFieldErrors();
    List<InvalidatedParams> validationResponse = errors.stream()
            .map(err -> InvalidatedParams.builder()
                    .cause(err.getDefaultMessage())
                    .attribute(err.getField())
                    .build()
            ).toList();

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
    problemDetail.setType(URI.create("validation-failed"));
    problemDetail.setTitle("Validation Failed");
    problemDetail.setProperty("invalidParams", validationResponse);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
  }

  private ProblemDetail createProblemDetail(HttpStatus status, String title, String type, String detail) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
    problemDetail.setType(URI.create(type));
    problemDetail.setTitle(title);
    return problemDetail;
  }
}