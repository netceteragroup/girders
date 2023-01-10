package com.netcetera.girders.csp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Servlet for handling (i.e. logging) Content-Security-Policy violation reports.
 */
@SuppressWarnings("SerializableDeserializableClassInSecureContext")
@Slf4j
public class CspViolationReportServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    logger.warn(IOUtils.toString(req.getInputStream(), "utf-8"));
  }

}
