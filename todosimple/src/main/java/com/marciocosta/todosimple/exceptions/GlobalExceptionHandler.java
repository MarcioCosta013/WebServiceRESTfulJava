package com.marciocosta.todosimple.exceptions;


import java.io.IOException;
import java.nio.file.AccessDeniedException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.marciocosta.todosimple.services.exceptions.AuthorizationException;
import com.marciocosta.todosimple.services.exceptions.DataBindingViolationException;
import com.marciocosta.todosimple.services.exceptions.ObjectNotFoundException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;


@RestControllerAdvice
@Slf4j(topic = "GLOBAL_EXCEPTON_HANDLER") //DO LOMBOK um log que printa no console as anotações da classe
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements AuthenticationFailureHandler{
    
    @Value("${server.error.include-exception}") //Bom para ambiente de desenvolvimento
    private boolean printStackTrace;


    @Override
    @ResponseStatus
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException,
     HttpHeaders headers,
     HttpStatusCode status,
      WebRequest request){
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Validation error, Check 'errors' field for details.");
        for (FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()){
            errorResponse.addVaidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    //Vai cair nessa excesão se der um erro que não tratamos, geral, retorna um erro padrão
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(
        Exception exception,
        WebRequest request) {
            final String errorMessage = "Unknown error occurred";
            log.error(errorMessage, exception);
            return buildErrorResponse(
                exception,
                errorMessage,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
            );
    }

    //Trata para se por exemlo caso eu queira criar um novo usuario com o mesmo 'username'(que foi definido como unico)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
        DataIntegrityViolationException dataIntegrityViolationException,
        WebRequest request) {
            String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
            log.error("Failed to save entity with integrity problems: " + errorMessage, dataIntegrityViolationException);
            return buildErrorResponse(dataIntegrityViolationException, errorMessage, HttpStatus.CONFLICT, request);
    }

    //Caso queria criar um usuario sem criar uma senha, ou visse versa
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleConstraintViolationException(
        ConstraintViolationException constraintViolationException,
        WebRequest request){
            log.error("Failed to validate element", constraintViolationException);
            return buildErrorResponse(
                constraintViolationException,
                HttpStatus.UNPROCESSABLE_ENTITY,
                request);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleObjectNotFoundException(
        ObjectNotFoundException objectNotFoundException,
        WebRequest request) {
            log.error("Failed to find the requested element", objectNotFoundException);
            return buildErrorResponse(objectNotFoundException, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(DataBindingViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataBindingViolationException(
        DataBindingViolationException dataBindingViolationException,
        WebRequest request) {
            log.error("Failed to save entity with associated data", dataBindingViolationException);
            return buildErrorResponse(dataBindingViolationException, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAutheticationException(
        AuthenticationException authenticationException,
        WebRequest request){
            log.error("Authentication error", authenticationException);
            return buildErrorResponse(authenticationException, HttpStatus.UNAUTHORIZED, request);
        }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAccessDeniedException (
            AccessDeniedException accessDeniedException,
            WebRequest request){
        log.error("Authetication", accessDeniedException);
        return buildErrorResponse(
            accessDeniedException,
            HttpStatus.FORBIDDEN,
            request);
    }

    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAuthorizationException(
        AuthorizationException authorizationException,
        WebRequest request){
            log.error("Authorization error", authorizationException);
            return buildErrorResponse(authorizationException, HttpStatus.FORBIDDEN, request);
        }

    private ResponseEntity<Object> buildErrorResponse(
        Exception exception,
        HttpStatus httpStatus,
        WebRequest request){
            return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
        }

    private ResponseEntity<Object> buildErrorResponse(
        Exception exception,
        String message,
        HttpStatus httpStatus,
        WebRequest request) {
            ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
            if (this.printStackTrace) {
                errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
            }
            return ResponseEntity.status(httpStatus).body(errorResponse);
        }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        Integer status = HttpStatus.UNAUTHORIZED.value(); //erro 401 - não autoriazado.
        response.setStatus(status);
        response.setContentType("application.json");
        ErrorResponse errorResponse = new ErrorResponse(status, "Email ou senha invalidos.");
        response.getWriter().append(errorResponse.toJson());

    }
}
