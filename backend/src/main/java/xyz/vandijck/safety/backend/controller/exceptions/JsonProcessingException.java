package xyz.vandijck.safety.backend.controller.exceptions;

public class JsonProcessingException extends BadRequestException
{
    public JsonProcessingException()
    {
        super("all", "unprocessable");
    }
}
