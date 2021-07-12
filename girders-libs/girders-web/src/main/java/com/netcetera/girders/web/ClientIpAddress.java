package com.netcetera.girders.web;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to half-reliably extract the "real" client IP address.
 */
public final class ClientIpAddress {

  // CHECKSTYLE:OFF
  // http://stackoverflow.com/a/11327345/131929
  private static final Pattern PRIVATE_ADDRESS_PATTERN = Pattern.compile(
      "(^127\\.)|(^192\\.168\\.)|(^10\\.)|(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.)|(^::1$)|"
          + "(^[fF][cCdD])", Pattern.CANON_EQ);
  // CHECKSTYLE:ON

  private ClientIpAddress() {
  }

  /**
   * Extracts the "real" client IP address from the request. It analyzes request headers
   * {@code REMOTE_ADDR}, {@code X-Forwarded-For} as well as {@code Client-IP}. Optionally
   * private/local addresses can be filtered in which case an empty string is returned.
   *
   * @param request                HTTP request
   * @param filterPrivateAddresses true if private/local addresses (see
   *                               https://en.wikipedia.org/wiki/Private_network#Private_IPv4_address_spaces and
   *                               https://en.wikipedia.org/wiki/Unique_local_address) should be filtered i.e. omitted
   *
   * @return IP address or empty string
   */
  public static String getFrom(HttpServletRequest request, boolean filterPrivateAddresses) {
    String ip = request.getRemoteAddr();

    String headerClientIp = request.getHeader("Client-IP");
    String headerXForwardedFor = request.getHeader("X-Forwarded-For");
    if (StringUtils.isEmpty(ip) && StringUtils.isNotEmpty(headerClientIp)) {
      ip = headerClientIp;
    } else if (StringUtils.isNotEmpty(headerXForwardedFor)) {
      ip = headerXForwardedFor;
    }
    return (ip == null || filterPrivateAddresses && isPrivateOrLocalAddress(ip)) ? StringUtils.EMPTY : ip;
  }

  private static boolean isPrivateOrLocalAddress(CharSequence address) {
    Matcher matcher = PRIVATE_ADDRESS_PATTERN.matcher(address);
    // matches sub-sequences, just as defined in the pattern
    return matcher.find();
  }

}
