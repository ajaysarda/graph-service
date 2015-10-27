package com.cyngn.omnistore.api.validate;

import com.cyngn.omnistore.api.request.DeleteConnectionRequest;
import com.cyngn.omnistore.api.request.DeleteObjectRequest;
import com.cyngn.omnistore.api.request.GetConnectionRequest;
import com.cyngn.omnistore.api.request.GetConnectionsRequest;
import com.cyngn.omnistore.api.request.GetObjectRequest;
import com.cyngn.omnistore.api.request.PutConnectionRequest;
import com.cyngn.omnistore.api.request.PutObjectRequest;
import com.cyngn.omnistore.registry.TypeRegistry;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * Request validator for all graph requests.
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/28/15.
 */
public class RequestValidator {
    private TypeRegistry registry;

    public RequestValidator(TypeRegistry registry) {
        this.registry = registry;
    }

    public ValidationResult validateRequest(GetObjectRequest request) {
        return validate(request.getScope(), request.getType(), request.getId());
    }

    public ValidationResult validateRequest(PutObjectRequest request) {
        return validate(request.getScope(), request.getType(), request.getId());
    }

    public ValidationResult validateRequest(DeleteObjectRequest request) {
        return validate(request.getScope(), request.getType(), request.getId());
    }

    public ValidationResult validateRequest(GetConnectionRequest request) {
        return validate(request.getScope(), request.getSourceType(), request.getSourceId(),
                request.getConnectionType(), request.getDestinationType(), request.getDestinationId());
    }

    public ValidationResult validateRequest(GetConnectionsRequest request) {
        return validate(request.getScope(), request.getSourceType(), request.getSourceId(), request.getConnectionType());
    }

    public ValidationResult validateRequest(PutConnectionRequest request) {
        return validate(request.getScope(), request.getSourceType(), request.getSourceId(),
                request.getConnectionType(), request.getDestinationType(), request.getDestinationId());
    }

    public ValidationResult validateRequest(DeleteConnectionRequest request) {
        return validate(request.getScope(), request.getSourceType(), request.getSourceId(),
                request.getConnectionType(), request.getDestinationType(), request.getDestinationId());
    }

    private ValidationResult validate(String scope, String type, String id) {

        if (scope != null && StringUtils.isEmpty(scope)) {
            return new ValidationResult(false, "scope cannot be empty");
        }

        ValidationResult result = validateEmptyParameters(type, id);

        if (!ValidationResult.SUCCESS.equals(result)) {
            return result;
        }

        if (!registry.isValidObjectType(type)) {
            return new ValidationResult(false, "Invalid object type : " + type);
        }

        return ValidationResult.SUCCESS;
    }

    private ValidationResult validate(String scope, String type, String id, String connType, String dstType, String dstId) {

        ValidationResult result = validate(scope, type, id, connType);

        if (!ValidationResult.SUCCESS.equals(result)) {
            return result;
        }
        if (dstType != null) {
            result = validateEmptyParameters(dstType, dstId);

            if (!ValidationResult.SUCCESS.equals(result)) {
                return result;
            }

            if (!registry.isValidObjectType(dstType)) {
                return new ValidationResult(false, "Invalid object type : " + dstType);
            } else {
                if (!registry.getConnectionType(connType).isValidConnectionTo(dstType)) {
                    return new ValidationResult(false, "Invalid connection type: " + connType + " for object type: " + dstType);
                }
            }
        }
        return ValidationResult.SUCCESS;
    }

    private ValidationResult validate(String scope, String type, String id, String connType) {

        ValidationResult result = validate(scope, type, id);

        if (!ValidationResult.SUCCESS.equals(result)) {
            return result;
        }

        // validate object and connection types
        if (!registry.isValidObjectType(type)) {
            return new ValidationResult(false, "Invalid object type : " + type);
        } else {
            if (StringUtils.isBlank(connType)) {
                return new ValidationResult(false, connType + " cannot be empty");
            }
            if (!registry.isValidConnectionType(connType)) {
                return new ValidationResult(false, "Invalid connection type: " + connType);
            }
            if (!registry.getConnectionType(connType).isValidConnectionFrom(type)) {
                return new ValidationResult(false, "Invalid connection type: " + connType + " for object type: " + type);
            }
        }

        return ValidationResult.SUCCESS;
    }

    private ValidationResult validateEmptyParameters(String... args) {
        Optional<String> invalidArgument =
                Arrays.asList(args).stream().filter(str -> StringUtils.isBlank(str)).findFirst();

        if (invalidArgument.isPresent()) {
            return new ValidationResult(false, invalidArgument.get() + "cannot be empty");
        }

        return ValidationResult.SUCCESS;
    }
}
