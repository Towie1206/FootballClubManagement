package com.eaut.footballclubmanagement.network;

import com.eaut.footballclubmanagement.models.Player;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ApiServiceContractTest {

    @Test
    public void playerEndpoints_useExpectedHttpMethodsAndPaths() throws Exception {
        assertEndpoint("getPlayers", new Class<?>[]{}, GET.class, "api/players");
        assertEndpoint("addPlayer", new Class<?>[]{Player.class}, POST.class, "api/players");
        assertEndpoint("updatePlayer", new Class<?>[]{int.class, Player.class}, PUT.class, "api/players/{id}");
        assertEndpoint("deletePlayer", new Class<?>[]{int.class}, DELETE.class, "api/players/{id}");

        assertPathParameter(ApiService.class.getMethod("updatePlayer", int.class, Player.class), 0, "id");
        assertPathParameter(ApiService.class.getMethod("deletePlayer", int.class), 0, "id");
    }

    @Test
    public void authenticationAndAiEndpoints_useExpectedContracts() throws Exception {
        assertEndpoint("askAiCoach", new Class<?>[]{ApiService.AiRequest.class}, POST.class, "api/ai/coach");
        assertEndpoint("login", new Class<?>[]{ApiService.LoginRequest.class}, POST.class, "api/auth/login");
        assertEndpoint("register", new Class<?>[]{ApiService.LoginRequest.class}, POST.class, "api/auth/register");
    }

    @Test
    public void requestDtos_serializeOnlyTheFieldsAcceptedByBackend() {
        Gson gson = new Gson();

        JsonObject login = new JsonParser().parse(
                gson.toJson(new ApiService.LoginRequest("captain", "safe-password"))
        ).getAsJsonObject();
        assertEquals(2, login.size());
        assertEquals("captain", login.get("username").getAsString());
        assertEquals("safe-password", login.get("password").getAsString());

        JsonObject ai = new JsonParser().parse(
                gson.toJson(new ApiService.AiRequest("Xếp đội hình sân 7"))
        ).getAsJsonObject();
        assertEquals(1, ai.size());
        assertEquals("Xếp đội hình sân 7", ai.get("prompt").getAsString());
    }

    @Test
    public void responseDtos_deserializeSuccessAndOptionalFields() {
        Gson gson = new Gson();

        ApiService.LoginResponse login = gson.fromJson(
                "{\"token\":\"jwt-token\",\"message\":\"Đăng nhập thành công\","
                        + "\"expiresIn\":3600,\"user\":{\"id\":7,\"username\":\"captain\",\"role\":\"manager\"}}",
                ApiService.LoginResponse.class
        );
        assertEquals("jwt-token", login.token);
        assertEquals("Đăng nhập thành công", login.message);
        assertEquals(3600L, login.expiresIn);
        assertNotNull(login.user);
        assertEquals(7, login.user.id);
        assertEquals("captain", login.user.username);
        assertEquals("manager", login.user.role);

        ApiService.AiResponse ai = gson.fromJson("{\"reply\":\"Đội hình đề xuất\"}", ApiService.AiResponse.class);
        assertEquals("Đội hình đề xuất", ai.reply);

        ApiService.LoginResponse register = gson.fromJson(
                "{\"message\":\"Đăng ký thành công\"}",
                ApiService.LoginResponse.class
        );
        assertEquals("Đăng ký thành công", register.message);
        assertNull(register.token);
        assertEquals(0L, register.expiresIn);
        assertNull(register.user);
    }

    private static void assertEndpoint(
            String methodName,
            Class<?>[] parameterTypes,
            Class<? extends Annotation> annotationType,
            String expectedPath
    ) throws Exception {
        Method method = ApiService.class.getMethod(methodName, parameterTypes);
        Annotation annotation = method.getAnnotation(annotationType);
        assertNotNull("Missing @" + annotationType.getSimpleName() + " on " + methodName, annotation);

        Method valueMethod = annotationType.getMethod("value");
        assertEquals(expectedPath, valueMethod.invoke(annotation));
    }

    private static void assertPathParameter(Method method, int parameterIndex, String expectedName) {
        Path path = null;
        for (Annotation annotation : method.getParameterAnnotations()[parameterIndex]) {
            if (annotation instanceof Path) {
                path = (Path) annotation;
                break;
            }
        }

        assertNotNull("Missing @Path on " + method.getName(), path);
        assertEquals(expectedName, path.value());
    }
}
