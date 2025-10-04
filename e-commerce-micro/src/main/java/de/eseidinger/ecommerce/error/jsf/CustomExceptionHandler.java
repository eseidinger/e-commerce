package de.eseidinger.ecommerce.error.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.ExceptionQueuedEventContext;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomExceptionHandler extends ExceptionHandlerWrapper {

  private static final Logger logger =
      LoggerFactory.getLogger(CustomExceptionHandler.class.getName());

  public CustomExceptionHandler(ExceptionHandler wrapped) {
    super(wrapped);
  }

  @Override
  public void handle() throws RuntimeException {
    Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();

    while (i.hasNext()) {
      ExceptionQueuedEvent event = i.next();
      ExceptionQueuedEventContext context = event.getContext();
      Throwable root = context.getException();
      while (root.getCause() != null && root != root.getCause()) {
        root = root.getCause();
      }

      try {
        logger.error("Unhandled exception", root);

        String exceptionName = root.getClass().getSimpleName();
        logger.debug("Exception class: {}", exceptionName);

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null && facesContext.getExternalContext().getRequestMap() != null) {
          facesContext.addMessage(
              null,
              new FacesMessage(FacesMessage.SEVERITY_ERROR, exceptionName, root.getMessage()));
        }
      } finally {
        i.remove(); // remove the handled exception
      }
    }

    getWrapped().handle();
  }
}
