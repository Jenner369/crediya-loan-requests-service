package co.com.crediya.r2dbc.config.converter;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UUIDConverterTest {
    private final UUIDWriteConverter writeConverter = new UUIDWriteConverter();
    private final UUIDReadConverter readConverter = new UUIDReadConverter();

    @Test
    void shouldConvertUuidToString() {
        UUID uuid = UUID.randomUUID();
        String result = writeConverter.convert(uuid);

        assertThat(result).isEqualTo(uuid.toString());
    }

    @Test
    void shouldConvertStringToUuid() {
        UUID uuid = UUID.randomUUID();
        UUID result = readConverter.convert(uuid.toString());

        assertThat(result).isEqualTo(uuid);
    }
}
