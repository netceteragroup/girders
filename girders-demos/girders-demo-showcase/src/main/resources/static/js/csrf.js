$("#csrf-javascript-form").submit(function (event) {
  event.preventDefault();
  $.ajax({
    type: "POST",
    beforeSend: function (request) {
      request.setRequestHeader("X-CSRF-TOKEN", $.cookie("CSRF-TOKEN"));
    },
    url: $("#csrf-javascript-form").attr("action"),
    success : function(data, textStatus, jqXHR) {
      alert("Submitting the form was successful");
    },
    error : function(data, textStatus, jqXHR) {
      alert("Submitting the form failed: " + data.responseText);
    }
  });
});
