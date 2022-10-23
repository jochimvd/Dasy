package xyz.vandijck.safety.backend.controller.exceptions;

public class NotFoundException extends BadRequestException {

    private static final long serialVersionUID = 4986047153270607191L;

    public NotFoundException(String entityType) {
        super(entityType, "notFound");
    }
}
