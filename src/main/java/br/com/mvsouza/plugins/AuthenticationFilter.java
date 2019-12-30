package br.com.mvsouza.plugins;

import java.io.IOException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author msouza
 */
public class AuthenticationFilter implements ClientRequestFilter {

    private final String apiKey;

    public AuthenticationFilter(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        headers.add("Software-Delivery-Api-Key", this.apiKey);
    }

}
