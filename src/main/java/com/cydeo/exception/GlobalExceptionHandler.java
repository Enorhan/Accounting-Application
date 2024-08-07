package com.cydeo.exception;

import com.cydeo.exceptions.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFoundException(UserNotFoundException ex) {
        return getModelAndView(ex.getMessage());
    }

    @ExceptionHandler(InvoiceProductNotFoundException.class)
    public ModelAndView handleInvoiceProductNotFoundException(InvoiceProductNotFoundException ex) {
        return getModelAndView(ex.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ModelAndView handleRoleNotFoundException(RoleNotFoundException ex) {
        return getModelAndView(ex.getMessage());
    }

    @ExceptionHandler(InvoiceNotFoundException.class)
    public ModelAndView handleInvoiceNotFoundException(InvoiceNotFoundException ex) {
        return getModelAndView(ex.getMessage());
    }

    @ExceptionHandler(ProductLowLimitAlertException.class)
    public ModelAndView handleProductLowLimitAlertException(ProductLowLimitAlertException ex){
        return getModelAndView(ex.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ModelAndView handleProductNotFoundException(ProductNotFoundException ex){
        return getModelAndView(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException() {
        return getModelAndView("An unexpected error occurred. Please try again later.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex) {
        ModelAndView modelAndView = new ModelAndView("redirect:/salesInvoices");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    private ModelAndView getModelAndView(String message) {
        ModelAndView modelAndView = new ModelAndView("error");
        if (message == null || message.isEmpty() || message.equals("No message available")) {
            modelAndView.addObject("message", "Something went wrong!");
        } else {
            modelAndView.addObject("message", message);
        }
        return modelAndView;
    }
}