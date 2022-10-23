package xyz.vandijck.safety.backend.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * Request that contains information the user has entered to confirm the deletion of an entity
 */
@Data
@Accessors( chain = true)
@NoArgsConstructor
public class DeleteRequest {

    @NotBlank
    private String confirmMessage;

    public DeleteRequest(String confirmMessage){
        this.confirmMessage = confirmMessage;
    }
}