package xyz.vandijck.safety.backend.entity;

public interface Archivable {
    boolean isArchived();

    <T extends Archivable> T setArchived(boolean archived);
}
