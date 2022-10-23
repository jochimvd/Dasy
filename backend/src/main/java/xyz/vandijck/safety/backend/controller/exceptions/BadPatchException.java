package xyz.vandijck.safety.backend.controller.exceptions;

public class BadPatchException extends BadRequestException
{
    private static final long serialVersionUID = -8992791346324783980L;

    public BadPatchException(String path)
    {
        super("path", "unpatchable: " + path);
    }
}
