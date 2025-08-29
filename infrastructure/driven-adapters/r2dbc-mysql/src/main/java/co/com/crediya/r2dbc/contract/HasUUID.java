package co.com.crediya.r2dbc.contract;

import java.util.UUID;

public interface HasUUID {
    UUID getId();
    void setId(UUID id);
}
