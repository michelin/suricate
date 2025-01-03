package com.michelin.suricate.service.js.script;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.util.exception.js.FatalException;
import com.michelin.suricate.util.exception.js.RemoteException;
import com.michelin.suricate.util.exception.js.RequestException;
import com.michelin.suricate.util.http.OkHttpClientUtils;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class JsEndpointsTest {
    @Mock
    private OkHttpClient client;

    @Mock
    private Call call;

    @Test
    void shouldGetSuccessful() throws IOException, RemoteException, RequestException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(HttpStatus.OK.value())
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = JsEndpoints.get("https://mocked.com");

            assertEquals("response", actual);

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.GET.toString())));
        }
    }

    @Test
    void shouldGetServerError() throws IOException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            RemoteException exception = assertThrows(
                RemoteException.class,
                () -> JsEndpoints.get("https://mocked.com")
            );

            assertEquals("A server error occurred during the execution of the request /GET https://mocked.com/ (code 500).",
                exception.getMessage());

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.GET.toString())));
        }
    }

    @Test
    void shouldGetRequestError() throws IOException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(HttpStatus.FORBIDDEN.value())
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            RequestException exception = assertThrows(
                RequestException.class,
                () -> JsEndpoints.get("https://mocked.com")
            );

            assertEquals("A request error occurred during the execution of the request /GET https://mocked.com/ "
                    + "(code 403). Error body details: response",
                exception.getMessage());

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.GET.toString())));
        }
    }

    @Test
    void shouldGetRequestErrorEmptyBody() throws IOException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(HttpStatus.FORBIDDEN.value())
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            RequestException exception = assertThrows(
                RequestException.class,
                () -> JsEndpoints.get("https://mocked.com")
            );

            assertEquals("A request error occurred during the execution of the request /GET https://mocked.com/ "
                + "(code 403). Error body details: Empty body", exception.getMessage());

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.GET.toString())));
        }
    }

    @Test
    void shouldGetReturnCode() throws IOException, RemoteException, RequestException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(200)
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = JsEndpoints.get("https://mocked.com", true);

            assertEquals("200", actual);

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.GET.toString())));
        }
    }

    @Test
    void shouldGetWithHeader() throws IOException, RemoteException, RequestException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(200)
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = JsEndpoints.get("https://mocked.com", "header", "headerValue");

            assertEquals("response", actual);

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.GET.toString())
                    && Objects.equals(request.header("header"), "headerValue")));
        }
    }

    @Test
    void shouldGetReturnCodeWithHeader() throws IOException, RemoteException, RequestException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(200)
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = JsEndpoints.get("https://mocked.com", "header", "headerValue", true);

            assertEquals("200", actual);

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.GET.toString())
                    && Objects.equals(request.header("header"), "headerValue")));
        }
    }

    @Test
    void shouldGetWithHeaderAndHeaderToReturn() throws IOException, RemoteException, RequestException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(200)
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .header("headerToReturn", "valueToReturn")
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = JsEndpoints.get("https://mocked.com", "header", "headerValue", "headerToReturn");

            assertEquals("valueToReturn", actual);

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.GET.toString())
                    && Objects.equals(request.header("header"), "headerValue")));
        }
    }

    @Test
    void shouldPost() throws IOException, RemoteException, RequestException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(200)
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = JsEndpoints.post("https://mocked.com", "{\"key\": \"value\"}");

            assertEquals("response", actual);

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.POST.toString())
                    && request.body() != null
                    && Objects.requireNonNull(request.body().contentType()).toString()
                    .equals("application/json; charset=utf-8")));
        }
    }

    @Test
    void shouldPostEmptyBody() throws IOException, RemoteException, RequestException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(200)
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = JsEndpoints.post("https://mocked.com", EMPTY);

            assertEquals("response", actual);

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.POST.toString())
                    && request.body() != null
                    && Objects.requireNonNull(request.body().contentType()).toString()
                    .equals("application/json; charset=utf-8")));
        }
    }

    @Test
    void shouldPostReturnCode() throws IOException, RemoteException, RequestException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(200)
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = JsEndpoints.post("https://mocked.com", "{\"key\": \"value\"}", true);

            assertEquals("200", actual);

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.POST.toString())
                    && request.body() != null
                    && Objects.requireNonNull(request.body().contentType()).toString()
                    .equals("application/json; charset=utf-8")));
        }
    }

    @Test
    void shouldPostEmptyBodyReturnCode() throws IOException, RemoteException, RequestException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(200)
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = JsEndpoints.post("https://mocked.com", EMPTY, true);

            assertEquals("200", actual);

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.POST.toString())
                    && request.body() != null
                    && Objects.requireNonNull(request.body().contentType()).toString()
                    .equals("application/json; charset=utf-8")));
        }
    }

    @Test
    void shouldPostWithHeader() throws IOException, RemoteException, RequestException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(200)
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = JsEndpoints.post("https://mocked.com", "{\"key\": \"value\"}", "header", "headerValue");

            assertEquals("response", actual);

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.POST.toString())
                    && request.body() != null
                    && Objects.requireNonNull(request.body().contentType()).toString()
                    .equals("application/json; charset=utf-8")
                    && Objects.equals(request.header("header"), "headerValue")));
        }
    }

    @Test
    void shouldPostEmptyBodyWithHeader() throws IOException, RemoteException, RequestException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(200)
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = JsEndpoints.post("https://mocked.com", EMPTY, "header", "headerValue");

            assertEquals("response", actual);

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.POST.toString())
                    && request.body() != null
                    && Objects.requireNonNull(request.body().contentType()).toString()
                    .equals("application/json; charset=utf-8")
                    && Objects.equals(request.header("header"), "headerValue")));
        }
    }

    @Test
    void shouldPostReturnCodeWithHeader() throws IOException, RemoteException, RequestException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(200)
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual =
                JsEndpoints.post("https://mocked.com", "{\"key\": \"value\"}", "header", "headerValue", true);

            assertEquals("200", actual);

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.POST.toString())
                    && request.body() != null
                    && Objects.requireNonNull(request.body().contentType()).toString()
                    .equals("application/json; charset=utf-8")
                    && Objects.equals(request.header("header"), "headerValue")));
        }
    }

    @Test
    void shouldPostEmptyBodyReturnCodeWithHeader() throws IOException, RemoteException, RequestException {
        try (MockedStatic<OkHttpClientUtils> mocked = mockStatic(OkHttpClientUtils.class)) {
            Response response = new Response.Builder()
                .code(200)
                .request(new Request.Builder()
                    .url("https://mocked.com")
                    .build())
                .body(ResponseBody.create("response",
                    MediaType.get(String.valueOf(org.springframework.http.MediaType.APPLICATION_JSON))))
                .protocol(Protocol.HTTP_2)
                .message(EMPTY)
                .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = JsEndpoints.post("https://mocked.com", EMPTY, "header", "headerValue", true);

            assertEquals("200", actual);

            verify(client)
                .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/")
                    && request.method().equals(HttpMethod.POST.toString())
                    && request.body() != null
                    && Objects.requireNonNull(request.body().contentType()).toString()
                    .equals("application/json; charset=utf-8")
                    && Objects.equals(request.header("header"), "headerValue")));
        }
    }

    @Test
    void shouldCheckInterrupted() {
        Thread.currentThread().interrupt();

        InterruptedException exception = assertThrows(
            InterruptedException.class,
            JsEndpoints::checkInterrupted
        );

        assertEquals("Script Interrupted", exception.getMessage());
    }

    @Test
    void shouldCheckInterruptedNotThrowException() {
        assertDoesNotThrow(JsEndpoints::checkInterrupted);
    }

    @Test
    void shouldBtoaNull() {
        String actual = JsEndpoints.btoa(null);
        assertNull(actual);
    }

    @Test
    void shouldBtoaEmpty() {
        String actual = JsEndpoints.btoa(EMPTY);
        assertNull(actual);
    }

    @Test
    void shouldBtoa() {
        String actual = JsEndpoints.btoa("test");
        assertEquals("dGVzdA==", actual);
    }

    @Test
    void shouldThrowError() {
        RemoteException exception = assertThrows(
            RemoteException.class,
            JsEndpoints::throwError
        );

        assertEquals("Error", exception.getMessage());
    }

    @Test
    void shouldThrowFatalError() {
        FatalException exception = assertThrows(
            FatalException.class,
            () -> JsEndpoints.throwFatalError("Error")
        );

        assertEquals("Error", exception.getMessage());
    }

    @Test
    void shouldThrowTimeout() {
        TimeoutException exception = assertThrows(
            TimeoutException.class,
            JsEndpoints::throwTimeout
        );

        assertEquals("Timeout", exception.getMessage());
    }
}
