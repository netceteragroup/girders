$(document).ready(function () {
  $.ajax({
    type: "POST",
    url: '/demo-showcase/log',
    data: JSON.stringify(
      {
        logLevel: "DEBUG",
        message: "Demo application is ready",
        url: window.location.href
      }),
    contentType: 'application/json',
    dataType: 'application/json',
    beforeSend: function (request) {
      request.setRequestHeader("X-CSRF-TOKEN", $.cookie("CSRF-TOKEN"));
    }
  });
});
