package config;

import com.rag.service.config.OpenAPIConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenAPIConfigTest {
    @Test
    void customOpenAPI_ShouldReturnConfiguredOpenAPI() {
        OpenAPIConfig config = new OpenAPIConfig();
        OpenAPI openAPI = config.customOpenAPI();
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        Info info = openAPI.getInfo();
        assertEquals("RAG Chat Storage API", info.getTitle());
        assertEquals("1.0", info.getVersion());
        assertNotNull(openAPI.getComponents().getSecuritySchemes().get("ApiKey"));
        SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("ApiKey");
        assertEquals(SecurityScheme.Type.APIKEY, scheme.getType());
        assertEquals(SecurityScheme.In.HEADER, scheme.getIn());
        assertEquals("X-API-Key", scheme.getName());
        assertNotNull(openAPI.getTags());
        assertTrue(openAPI.getTags().stream().anyMatch(tag -> "User Management".equals(tag.getName())));
    }
}

