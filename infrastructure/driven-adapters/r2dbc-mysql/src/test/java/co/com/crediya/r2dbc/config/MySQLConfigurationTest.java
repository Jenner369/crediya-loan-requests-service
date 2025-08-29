package co.com.crediya.r2dbc.config;

import co.com.crediya.r2dbc.config.converter.UUIDReadConverter;
import co.com.crediya.r2dbc.config.converter.UUIDWriteConverter;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MySQLConfigurationTest {

    @Test
    void shouldReturnConnectionFactory() {
        ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);

        MySQLConfiguration config = new MySQLConfiguration(mockConnectionFactory);

        assertThat(config.connectionFactory()).isEqualTo(mockConnectionFactory);
    }

    @Test
    void shouldRegisterCustomConverters() {
        ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);

        MySQLConfiguration config = new MySQLConfiguration(mockConnectionFactory);
        List<Object> converters = config.getCustomConverters();

        assertThat(converters)
                .hasSize(2)
                .anyMatch(UUIDReadConverter.class::isInstance)
                .anyMatch(UUIDWriteConverter.class::isInstance);
    }
}
