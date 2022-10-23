package xyz.vandijck.safety.backend.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@MappedSuperclass
@Accessors(chain = true)
public abstract class TokenBase
{
    public static final int NUM_BYTES_SECRET = 64;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @NotNull
    @Column(length = NUM_BYTES_SECRET)
    protected byte[] token;

    @NotNull
    protected Date expiration;

    public abstract TokenBase setUser(User user);
}
