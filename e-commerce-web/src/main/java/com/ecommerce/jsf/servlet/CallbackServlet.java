package com.ecommerce.jsf.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import fish.payara.security.openid.api.OpenIdContext;
import jakarta.inject.Inject;
import jakarta.security.enterprise.authentication.mechanism.http.openid.OpenIdConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/callback")
public class CallbackServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(CallbackServlet.class.getName());

    @Inject
    OpenIdContext context;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("CallbackServlet: doGet() called");
        //Here's the caller groups
        logger.info(context.getCallerGroups().toString());
        //Here's the unique subject identifier within the issuer
        logger.info(context.getSubject().toString());
        //Here's the access token
        logger.info(context.getAccessToken().toString());
        //Here's the identity token
        logger.info(context.getIdentityToken().toString());
        //Here's the user claims
        logger.info(context.getClaimsJson().toString());
        //Redirect back to the original request URL
        response.sendRedirect(request.getSession().getAttribute(OpenIdConstant.ORIGINAL_REQUEST).toString());
    }
}