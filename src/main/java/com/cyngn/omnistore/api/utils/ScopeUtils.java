package com.cyngn.omnistore.api.utils;

/**
 * Utility method for Scope
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/29/15.
 */
public class ScopeUtils {
    public final static String DEFAULT_SCOPE = "";

    public static String getScope(String scope) {
        if (scope == null) { return DEFAULT_SCOPE; }

        return scope;
    }

}
