package com.cydeo.exception;

import com.cydeo.exceptions.InvoiceNotFoundException;
import com.cydeo.exceptions.InvoiceProductNotFoundException;
import com.cydeo.exceptions.RoleNotFoundException;
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

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException() {
        return getModelAndView("An unexpected error occurred. Please try again later.");
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