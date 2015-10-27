package com.cyngn.omnistore.api.endpoint;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpServerResponse;
import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * @author truelove@cyngn.com (Jeremy Truelove) 4/13/15
 */
public class TestUtil {

    public static class ResponseCapture {
        public final ArgumentCaptor<Integer> responseCode;
        public final ArgumentCaptor<Buffer> responseBody;

        public ResponseCapture(ArgumentCaptor<Integer> responseCode, ArgumentCaptor<Buffer> responseBody) {
            this.responseCode = responseCode;
            this.responseBody = responseBody;
        }
    }

    public static ArgumentCaptor<Integer> setupResponse(HttpServerResponse response) {
        when(response.headers()).thenReturn(new CaseInsensitiveHeaders());
        doAnswer(invocationOnMock -> null).when(response).end();
        when(response.putHeader(any(CharSequence.class), any(CharSequence.class))).thenReturn(response);
        when(response.write(any(Buffer.class))).thenReturn(response);

        ArgumentCaptor<Integer> httpResponseCode = ArgumentCaptor.forClass(int.class);
        when(response.setStatusCode(httpResponseCode.capture())).thenReturn(response);

        return httpResponseCode;
    }

    public static ResponseCapture setupResponseFullCapture(HttpServerResponse response) {
        when(response.headers()).thenReturn(new CaseInsensitiveHeaders());
        doAnswer(invocationOnMock -> null).when(response).end();
        when(response.putHeader(any(CharSequence.class), any(CharSequence.class))).thenReturn(response);


        ArgumentCaptor<Buffer> body = ArgumentCaptor.forClass(Buffer.class);
        when(response.write(body.capture())).thenReturn(response);

        ArgumentCaptor<Integer> httpResponseCode = ArgumentCaptor.forClass(int.class);
        when(response.setStatusCode(httpResponseCode.capture())).thenReturn(response);

        return new ResponseCapture(httpResponseCode, body);
    }
}
