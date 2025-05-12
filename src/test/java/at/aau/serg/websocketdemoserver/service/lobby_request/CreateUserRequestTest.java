package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.service.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateUserRequestTest {

    private UserManager userManager;
    private CreateUserRequest request;

    @BeforeEach
    void setUp() {
        userManager = mock(UserManager.class);
        request = new CreateUserRequest(userManager);
    }

    @Test
    void testHandle_successfulUserCreation() {
        // Given
        PlayerDTO mockPlayer = new PlayerDTO(1, "TestUser");
        when(userManager.createUser("TestUser")).thenReturn(mockPlayer);

        LobbyMessage message = new LobbyMessage(
                0,
                LobbyMessageType.CREATE_USER,
                Map.of("username", "TestUser")
        );

        // When
        List<LobbyMessage> response = request.handle(message);

        // Then
        assertEquals(1, response.size());
        LobbyMessage result = response.get(0);
        assertEquals(LobbyMessageType.USER_CREATED, result.getType());

        @SuppressWarnings("unchecked")
        Map<String, String> payload = (Map<String, String>) result.getPayload();
        assertEquals("1", payload.get("playerId"));
        assertEquals("TestUser", payload.get("username"));
    }

    @Test
    void testHandle_withInvalidPayload_returnsError() {
        LobbyMessage message = new LobbyMessage(
                0,
                LobbyMessageType.CREATE_USER,
                "invalid"
        );

        List<LobbyMessage> response = request.handle(message);

        assertEquals(1, response.size());
        LobbyMessage result = response.get(0);
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Error creating user"));
    }
}
