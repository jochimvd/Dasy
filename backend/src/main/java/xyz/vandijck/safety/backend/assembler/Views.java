package xyz.vandijck.safety.backend.assembler;

import xyz.vandijck.safety.backend.entity.Role;

import java.util.EnumMap;
import java.util.Map;

/**
 * Defines the necessary classes for the json views on the dto's.
 * See the class definition of a dto for example usage of these classes.
 */
public class Views {

    // Mapping from the role to the json view
    private static final EnumMap<Role, Class<?>> MAPPING = new EnumMap<>(Role.class);

    static {
        MAPPING.put(Role.READER, Reader.class);
        MAPPING.put(Role.EDITOR, Editor.class);
        MAPPING.put(Role.ADMIN, Admin.class);
        MAPPING.put(Role.PERSONAL, Personal.class);
    }

    // The interfaces below have the same meaning as the roles, but are now defined on a dto field level.
    // Interfaces allow for more easy extension, should more complicated hierarchies be added


    public static Map<Role, Class<?>> getMapping() {
        return MAPPING;
    }

    public interface Reader {}

    // User can see everything a Reader can see
    public interface Personal extends Reader {}

    public interface Editor extends Reader {}

    public interface Admin extends Editor {}

}
