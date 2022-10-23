package xyz.vandijck.safety.backend.entity;

/**
 * Simple interface to facilitate more generic testing
 * Allows accessing id values from any entity in the database
 */
public interface UniqueEntity {

    long getId();

    <E extends UniqueEntity> E setId(long id);

}
