package xyz.vandijck.safety.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.controller.exceptions.BadPatchException;

import java.util.List;

/**
 * Generic patch request handler
 */
@Component
public class Patcher {

    // Mapper to map from and to the class of the generic type
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Validates the patch, check if the fields are allowed.
     *
     * @param patch           The patch request.
     * @param patchableFields The fields that are allowed to be patched
     */
    public boolean validatePatch(JsonPatch patch, List<String> patchableFields) {
        JsonNode toValidate = objectMapper.convertValue(patch, JsonNode.class);
        for (JsonNode node : toValidate) {
            String path = node.get("path").toString();
            boolean found = false;
            for (String allowed : patchableFields) {
                if (path.contains(allowed)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    /**
     * First validates and then applies the patch.
     *
     * @param vClass The class of the patched object
     * @param patch  The patch request.
     * @param target The target to be patched.
     * @return The object after applying the patch.
     * @throws BadPatchException When the Patch is not valid
     */
    public <V> V applyPatch(Class<V> vClass, JsonPatch patch, V target) throws BadPatchException {
        try {
            JsonNode patched = patch.apply(objectMapper.convertValue(target, JsonNode.class));
            return objectMapper.treeToValue(patched, vClass);
        } catch (JsonProcessingException | JsonPatchException e) {
            throw new BadPatchException(patch.toString());
        }
    }

    // Setter for unit testing purposes
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
