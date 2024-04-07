package com.michelin.suricate.service.js.script;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

            assertThat(actual).isEqualTo("response");

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

            assertThatThrownBy(() -> JsEndpoints.get("https://mocked.com"))
                .isInstanceOf(RemoteException.class)
                .hasMessage(
                    "A server error occurred during the execution of the request /GET https://mocked.com/ (code 500).");

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

            assertThatThrownBy(() -> JsEndpoints.get("https://mocked.com"))
                .isInstanceOf(RequestException.class)
                .hasMessage(
                    "A request error occurred during the execution of the request /GET https://mocked.com/ (code 403). Error body details: response");

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

            assertThatThrownBy(() -> JsEndpoints.get("https://mocked.com"))
                .isInstanceOf(RequestException.class)
                .hasMessage(
                    "A request error occurred during the execution of the request /GET https://mocked.com/ (code 403). Error body details: Empty body");

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

            assertThat(actual).isEqualTo("200");

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

            assertThat(actual).isEqualTo("response");

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

            assertThat(actual).isEqualTo("200");

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

            assertThat(actual).isEqualTo("valueToReturn");

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

            assertThat(actual).isEqualTo("response");

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

            assertThat(actual).isEqualTo("response");

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

            assertThat(actual).isEqualTo("200");

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

            assertThat(actual).isEqualTo("200");

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

            assertThat(actual).isEqualTo("response");

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

            assertThat(actual).isEqualTo("response");

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

            assertThat(actual).isEqualTo("200");

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

            assertThat(actual).isEqualTo("200");

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
        assertThatThrownBy(JsEndpoints::checkInterrupted)
            .isInstanceOf(InterruptedException.class)
            .hasMessage("Script Interrupted");
    }

    @Test
    void shouldCheckInterruptedNotThrowException() {
        assertThatCode(JsEndpoints::checkInterrupted).doesNotThrowAnyException();
    }

    @Test
    void shouldBtoaNull() {
        String actual = JsEndpoints.btoa(null);
        assertThat(actual).isNull();
    }

    @Test
    void shouldBtoaEmpty() {
        String actual = JsEndpoints.btoa(EMPTY);
        assertThat(actual).isNull();
    }

    @Test
    void shouldBtoa() {
        String actual = JsEndpoints.btoa("test");
        assertThat(actual).isEqualTo("dGVzdA==");
    }

    @Test
    void shouldThrowError() {
        assertThatThrownBy(JsEndpoints::throwError)
            .isInstanceOf(RemoteException.class)
            .hasMessage("Error");
    }

    @Test
    void shouldThrowFatalError() {
        assertThatThrownBy(() -> JsEndpoints.throwFatalError("Error"))
            .isInstanceOf(FatalException.class)
            .hasMessage("Error");
    }

    @Test
    void shouldThrowTimeout() {
        assertThatThrownBy(JsEndpoints::throwTimeout)
            .isInstanceOf(TimeoutException.class)
            .hasMessage("Timeout");
    }
}
