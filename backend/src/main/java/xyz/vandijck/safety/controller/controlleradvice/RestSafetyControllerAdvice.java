package xyz.vandijck.safety.controller.controlleradvice;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import xyz.vandijck.safety.exception.ResourceDoesNotExistException;

@ControllerAdvice
class RestSafetyControllerAdvice {

	@ExceptionHandler(IllegalArgumentException.class)
	void handleIllegalArgumentException(
			IllegalArgumentException ex, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
	}

	@ExceptionHandler(ResourceDoesNotExistException.class)
	void handleResourceDoesNotExistException(
			ResourceDoesNotExistException ex, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value(),
				"The resource '" + request.getRequestURI() + "' does not exist");
	}

}
