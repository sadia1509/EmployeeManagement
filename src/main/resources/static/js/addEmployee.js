$(document).ready(function () {
    // Initialize the datepicker
    $('.datepicker').datepicker({
        format: 'yyyy-mm-dd', // Date format that your backend expects
        autoclose: true,
        todayHighlight: true
    });

    $("#addEmployeeForm").on("submit", function (event) {
        event.preventDefault();
        const formData = new FormData(this);

        $.ajax({
            url: "/api/employee",
            type: "POST",
            data: formData,
            processData: false, // Prevent jQuery from processing the data
            contentType: false, // Prevent jQuery from setting the content-type header
            success: function (response) {
                alert(response); // Display success message
                location.reload(); // Optionally reload or redirect to another page
            },
            error: function (xhr, status, error) {
                alert("Error: " + xhr.responseText); // Display error message
            }
        });
    });
});