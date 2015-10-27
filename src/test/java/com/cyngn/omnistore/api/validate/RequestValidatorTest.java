package com.cyngn.omnistore.api.validate;

import com.cyngn.omnistore.api.request.GetObjectRequest;
import com.cyngn.omnistore.registry.TypeRegistry;
import org.apache.commons.lang.RandomStringUtils;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author asarda@cyngn.com (Ajay Sarda) on 8/29/15.
 */
public class RequestValidatorTest {
    private RequestValidator validator;

    @Before
    public void setup() {
        TypeRegistry mockRegistry = mock(TypeRegistry.class);
        when(mockRegistry.isValidConnectionType(Mockito.anyString())).thenReturn(true);
        when(mockRegistry.isValidObjectType(Mockito.anyString())).thenReturn(true);
        validator = new RequestValidator(mockRegistry);
    }

    @Test
    public void testValidGetObjectRequest() {
        GetObjectRequest request = new GetObjectRequest();
        request.setId(RandomStringUtils.randomAlphanumeric(10));
        request.setType(RandomStringUtils.randomAlphabetic(5));

        assertTrue(ValidationResult.SUCCESS.equals(validator.validateRequest(request)));
    }

    @Test
    public void testInvalidGetObjectRequest() {
        GetObjectRequest request = new GetObjectRequest();
        request.setId(RandomStringUtils.randomAlphanumeric(10));
        request.setType("");
        assertFalse(validator.validateRequest(request).valid);
    }

}