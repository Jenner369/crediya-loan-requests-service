package co.com.crediya.r2dbc.config;

import co.com.crediya.r2dbc.config.converter.UUIDReadConverter;
import co.com.crediya.r2dbc.config.converter.UUIDWriteConverter;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import java.util.List;

@Configuration
public class MySQLConfiguration extends AbstractR2dbcConfiguration {

    private final ConnectionFactory connectionFactory;

    public MySQLConfiguration(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public ConnectionFactory connectionFactory() {
        return this.connectionFactory;
    }

    @Override
    protected List<Object> getCustomConverters() {
        return List.of(new UUIDReadConverter(), new UUIDWriteConverter());
    }
}
