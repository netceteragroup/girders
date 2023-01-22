package com.netcetera.girders.web;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests ClientIpAddress.
 */
class ClientIpAddressTest {

  private static final String PUBLIC_IP = "194.126.3.4";
  private static final String LOOPBACK = "127.0.0.1";

  /**
   * See method name.
   */
  @Test
  void shouldReturnEmptyStringIfNoIpIsSet() {
    // given
    HttpServletRequest request = createMockRequest(null, null, null);
    // when
    String ipAddress = ClientIpAddress.getFrom(request, false);
    // then
    assertThat(ipAddress, is(StringUtils.EMPTY));
  }

  private static HttpServletRequest createMockRequest(String remoteAddr, String clientIp, String xForwardedFor) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    given(request.getRemoteAddr()).willReturn(remoteAddr);
    given(request.getHeader("Client-IP")).willReturn(clientIp);
    given(request.getHeader("X-Forwarded-For")).willReturn(xForwardedFor);
    return request;
  }

  /**
   * See method name.
   */
  @Test
  void shouldReturnRemoteAddrIfNoOtherIpHeaderIsSet() {
    // given
    HttpServletRequest request = createMockRequest(LOOPBACK, null, null);
    // when
    String ipAddress = ClientIpAddress.getFrom(request, false);
    // then
    assertThat(ipAddress, is(LOOPBACK));
  }

  /**
   * See method name.
   */
  @Test
  void shouldUseClientIpIfRemoteAddrMissing() {
    // given
    HttpServletRequest request = createMockRequest(null, LOOPBACK, null);
    // when
    String ipAddress = ClientIpAddress.getFrom(request, false);
    // then
    assertThat(ipAddress, is(LOOPBACK));
  }

  /**
   * See method name.
   */
  @Test
  void shouldUseForwardedIpIfSet() {
    // given
    HttpServletRequest request = createMockRequest(PUBLIC_IP, null, LOOPBACK);
    // when
    String ipAddress = ClientIpAddress.getFrom(request, false);
    // then
    assertThat(ipAddress, is(LOOPBACK));
  }

  /**
   * See method name.
   */
  @Test
  void shouldFilterLocalAddressIfRequested() {
    // given
    HttpServletRequest request = createMockRequest(LOOPBACK, null, null);
    // when
    String ipAddress = ClientIpAddress.getFrom(request, true);
    // then
    assertThat(ipAddress, is(StringUtils.EMPTY));
  }

  /**
   * See method name.
   */
  @Test
  void shouldNotFilterPublicIp() {
    // given
    HttpServletRequest request = createMockRequest(PUBLIC_IP, null, null);
    // when
    String ipAddress = ClientIpAddress.getFrom(request, true);
    // then
    assertThat(ipAddress, is(PUBLIC_IP));
  }
}
