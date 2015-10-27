package com.cyngn.omnistore.api.endpoint;

import com.cyngn.omnistore.api.request.GetObjectRequest;
import com.cyngn.omnistore.api.validate.RequestValidator;
import com.cyngn.omnistore.api.validate.ValidationResult;
import com.cyngn.omnistore.storage.ObjectStorage;
import com.cyngn.omnistore.storage.dataobjects.ObjectDO;
import com.google.common.util.concurrent.FutureCallback;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author asarda@cyngn.com (Ajay Sarda) on 8/29/15.
 */
public class GetObjectEndpointTest {

    @Test
    @Ignore
    public void test() throws Exception {
        RequestValidator validator = mock(RequestValidator.class);
        when(validator.validateRequest(Mockito.any(GetObjectRequest.class))).thenReturn(ValidationResult.SUCCESS);

        FutureCallback<ObjectDO> callback = mock(FutureCallback.class);

        //String type, String id, FutureCallback< ObjectDO > callback) {
        ObjectStorage storage = mock(ObjectStorage.class);

        Mockito.doAnswer(
                new Answer<Object>() {
                    public Object answer(InvocationOnMock invocation) {
                        ((FutureCallback<ObjectDO>) invocation.getArguments()[2]).onSuccess(null);
                        return null;
                    }
                }
        ).when(storage).getObject(anyString(), anyString(), anyObject());

        GetObjectEndpoint getObjectEndpoint = new GetObjectEndpoint(validator, storage);

        HttpServerResponse response = mock(HttpServerResponse.class);
        TestUtil.ResponseCapture capture = TestUtil.setupResponseFullCapture(response);

        GetObjectRequest request = new GetObjectRequest();
        request.setId(RandomStringUtils.randomAlphanumeric(10));
        request.setType(RandomStringUtils.randomAlphabetic(5));

        getObjectEndpoint.handle(request, response);

        Thread.sleep(5000L);

        assertTrue(capture.responseCode.getValue() == HttpResponseStatus.OK.code());


        
    }
}