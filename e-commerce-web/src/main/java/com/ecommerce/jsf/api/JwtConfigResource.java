package com.ecommerce.jsf.api;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import com.ecommerce.jsf.auth.OpenIdConfigBean;

@Path("/auth/config")
@RequestScoped
public class JwtConfigResource {

    @Inject
    private OpenIdConfigBean openIdConfigBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJwtConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("authHost", openIdConfigBean.getAuthHost());
        config.put("realm", openIdConfigBean.getAuthRealm());
        config.put("clientId", openIdConfigBean.getClientId());
        return Response.ok(config).build();
    }
}
