package xyz.vandijck.safety.backend.controller.exceptions;

public class ObservationNotFoundException extends NotFoundException {

    private static final long serialVersionUID = -2178788408995438481L;

    public ObservationNotFoundException() {
        super("Observation");
    }

}
