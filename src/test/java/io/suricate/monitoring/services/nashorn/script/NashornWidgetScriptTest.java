package io.suricate.monitoring.services.nashorn.script;

import io.suricate.monitoring.utils.exceptions.nashorn.FatalException;
import io.suricate.monitoring.utils.exceptions.nashorn.RemoteException;
import io.suricate.monitoring.utils.exceptions.nashorn.RequestException;
import io.suricate.monitoring.utils.http.OkHttpClientUtils;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class NashornWidgetScriptTest {
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
                    .message(StringUtils.EMPTY)
                    .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = NashornWidgetScript.get("https://mocked.com");

            assertThat(actual).isEqualTo("response");

            verify(client, times(1))
                    .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/") &&
                            request.method().equals(HttpMethod.GET.toString())));
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
                    .message(StringUtils.EMPTY)
                    .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            assertThatThrownBy(() -> NashornWidgetScript.get("https://mocked.com"))
                    .isInstanceOf(RemoteException.class)
                    .hasMessage("A server error occurred during the execution of the request /GET https://mocked.com/ (code 500).");

            verify(client, times(1))
                    .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/") &&
                            request.method().equals(HttpMethod.GET.toString())));
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
                    .message(StringUtils.EMPTY)
                    .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            assertThatThrownBy(() -> NashornWidgetScript.get("https://mocked.com"))
                    .isInstanceOf(RequestException.class)
                    .hasMessage("A request error occurred during the execution of the request /GET https://mocked.com/ (code 403). Error body details: response");

            verify(client, times(1))
                    .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/") &&
                            request.method().equals(HttpMethod.GET.toString())));
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
                    .message(StringUtils.EMPTY)
                    .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = NashornWidgetScript.get("https://mocked.com", true);

            assertThat(actual).isEqualTo("200");

            verify(client, times(1))
                    .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/") &&
                            request.method().equals(HttpMethod.GET.toString())));
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
                    .message(StringUtils.EMPTY)
                    .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = NashornWidgetScript.get("https://mocked.com", "header", "headerValue");

            assertThat(actual).isEqualTo("response");

            verify(client, times(1))
                    .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/") &&
                            request.method().equals(HttpMethod.GET.toString()) &&
                            Objects.equals(request.header("header"), "headerValue")));
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
                    .message(StringUtils.EMPTY)
                    .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = NashornWidgetScript.get("https://mocked.com", "header", "headerValue", true);

            assertThat(actual).isEqualTo("200");

            verify(client, times(1))
                    .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/") &&
                            request.method().equals(HttpMethod.GET.toString()) &&
                            Objects.equals(request.header("header"), "headerValue")));
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
                    .message(StringUtils.EMPTY)
                    .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = NashornWidgetScript.get("https://mocked.com", "header", "headerValue", "headerToReturn");

            assertThat(actual).isEqualTo("valueToReturn");

            verify(client, times(1))
                    .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/") &&
                            request.method().equals(HttpMethod.GET.toString()) &&
                            Objects.equals(request.header("header"), "headerValue")));
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
                    .message(StringUtils.EMPTY)
                    .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = NashornWidgetScript.post("https://mocked.com", "{\"key\": \"value\"}");

            assertThat(actual).isEqualTo("response");

            verify(client, times(1))
                    .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/") &&
                            request.method().equals(HttpMethod.POST.toString()) &&
                            request.body() != null &&
                            Objects.requireNonNull(request.body().contentType()).toString().equals("application/json; charset=utf-8")));
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
                    .message(StringUtils.EMPTY)
                    .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = NashornWidgetScript.post("https://mocked.com", "{\"key\": \"value\"}", true);

            assertThat(actual).isEqualTo("200");

            verify(client, times(1))
                    .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/") &&
                            request.method().equals(HttpMethod.POST.toString()) &&
                            request.body() != null &&
                            Objects.requireNonNull(request.body().contentType()).toString().equals("application/json; charset=utf-8")));
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
                    .message(StringUtils.EMPTY)
                    .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = NashornWidgetScript.post("https://mocked.com", "{\"key\": \"value\"}", "header", "headerValue");

            assertThat(actual).isEqualTo("response");

            verify(client, times(1))
                    .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/") &&
                            request.method().equals(HttpMethod.POST.toString()) &&
                            request.body() != null &&
                            Objects.requireNonNull(request.body().contentType()).toString().equals("application/json; charset=utf-8") &&
                            Objects.equals(request.header("header"), "headerValue")));
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
                    .message(StringUtils.EMPTY)
                    .build();

            mocked.when(OkHttpClientUtils::getUnsafeOkHttpClient).thenReturn(client);
            when(client.newCall(any())).thenReturn(call);
            when(call.execute()).thenReturn(response);

            String actual = NashornWidgetScript.post("https://mocked.com", "{\"key\": \"value\"}", "header", "headerValue", true);

            assertThat(actual).isEqualTo("200");

            verify(client, times(1))
                    .newCall(argThat(request -> request.url().toString().equals("https://mocked.com/") &&
                            request.method().equals(HttpMethod.POST.toString()) &&
                            request.body() != null &&
                            Objects.requireNonNull(request.body().contentType()).toString().equals("application/json; charset=utf-8") &&
                            Objects.equals(request.header("header"), "headerValue")));
        }
    }

    @Test
    void shouldCheckInterrupted() {
        Thread.currentThread().interrupt();
        assertThatThrownBy(NashornWidgetScript::checkInterrupted)
                .isInstanceOf(InterruptedException.class)
                .hasMessage("Script Interrupted");
    }

    @Test
    void shouldCheckInterruptedNotThrowException() {
        assertThatCode(NashornWidgetScript::checkInterrupted).doesNotThrowAnyException();
    }

    @Test
    void shouldBtoaNull() {
        String actual = NashornWidgetScript.btoa(null);
        assertThat(actual).isNull();
    }

    @Test
    void shouldBtoaEmpty() {
        String actual = NashornWidgetScript.btoa(StringUtils.EMPTY);
        assertThat(actual).isNull();
    }

    @Test
    void shouldBtoa() {
        String actual = NashornWidgetScript.btoa("test");
        assertThat(actual).isEqualTo("dGVzdA==");
    }

    @Test
    void shouldThrowError() {
        assertThatThrownBy(NashornWidgetScript::throwError)
                .isInstanceOf(RemoteException.class)
                .hasMessage("Error");
    }

    @Test
    void shouldThrowFatalError() {
        assertThatThrownBy(() -> NashornWidgetScript.throwFatalError("Error"))
                .isInstanceOf(FatalException.class)
                .hasMessage("Error");
    }

    @Test
    void shouldThrowTimeout() {
        assertThatThrownBy(NashornWidgetScript::throwTimeout)
                .isInstanceOf(TimeoutException.class)
                .hasMessage("Timeout");
    }
}
