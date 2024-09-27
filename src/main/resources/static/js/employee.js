$(document).ready(function () {

    let currentPage = 0; // Start from page 0
    let pageSize = $('#pageSize').val(); // Default page size from the dropdown

    loadEmployees(currentPage, pageSize);

    // Event handler for pagination buttons
    $('#nextPage').click(function () {
        currentPage++;
        loadEmployees(currentPage, pageSize);
    });

    $('#prevPage').click(function () {
        if (currentPage > 0) {
            currentPage--;
            loadEmployees(currentPage, pageSize);
        }
    });

    // Event handler for page size change
    $('#pageSize').change(function () {
        pageSize = $(this).val(); // Get the selected page size
        currentPage = 0; // Reset to first page
        loadEmployees(currentPage, pageSize);
    });

    // Event handler for sort buttons
    $('.sort-btn').click(function () {
        var column = $(this).data('column'); // Get the column to sort by
        var direction = $(this).data('direction'); // Get the current sort direction

        // Toggle direction (asc to desc, or desc to asc)
        direction = direction === 'asc' ? 'desc' : 'asc';
        $(this).data('direction', direction); // Update the button's data attribute

        // loading employees
        loadEmployees(currentPage, pageSize, column, direction);
    });

    // Function to load employees dynamically with pagination, sorting, and image handling
    function loadEmployees(offset = 0, pageSize = 10, sortBy = 'fullName', direction = 'asc') {
        $.ajax({
            url: '/api/employee/pagination-and-sort/' + offset + '/' + pageSize + '/' + sortBy + '/' + direction,
            type: 'GET',
            success: function (data) {
                var employeeTable = $('#employeeTable');
                employeeTable.empty(); // Clear previous content
                // Assuming 'data' contains an array of employee objects and pagination info
                data.content.forEach(function (employee) {
                    loadTheEmployees(employee, employeeTable);
                });

                // Handle pagination UI here
                $('#pageInfo').text(`Page ${data.number + 1} of ${data.totalPages}`);

                // Enable/disable pagination buttons
                $('#prevPage').prop('disabled', data.number === 0);
                $('#nextPage').prop('disabled', data.number + 1 >= data.totalPages);
            },
            error: function () {
                alert('Failed to fetch employee data.');
            }
        });
    }

    // load employee
    function loadTheEmployees(employee, employeeTable) {
        var row = `<tr>
            <td><img src="${employee.photo != null ? employee.photo : '/media/default-profile-pic.png'}" alt="Photo" style="width: 50px; height: 50px;"></td>
            <td>${employee.fullName}</td>
            <td>${employee.email}</td>
            <td>${employee.mobile}</td>
            <td>${employee.dateOfBirth}</td>
            <td>
                <button class="btn btn-sm btn-primary edit-btn" data-id="${employee.id}">Edit</button>
                <button class="btn btn-sm btn-danger delete-btn" data-id="${employee.id}">Delete</button>
            </td>
        </tr>`;
        employeeTable.append(row);
    }

    // Function to handle employee deletion with confirmation
    function editEmployee(employeeId) {
        // Fetch the employee details to populate the edit form
        $.ajax({
            url: '/api/employee/' + employeeId,
            type: 'GET',
            success: function (employee) {
                // Populate the form fields with employee data
                $('#editEmployeeId').val(employee.id);
                $('#editFullName').val(employee.fullName);
                $('#editEmail').val(employee.email);
                $('#editMobile').val(employee.mobile);
                $('#editDOB').val(employee.dateOfBirth);
                $('#editPhotoView').attr('src', employee.photo);

                // Show the modal
                $('#editEmployeeModal').modal('show');
            },
            error: function () {
                alert('Failed to load employee details.');
            }
        });
    }

    // Function to handle employee deletion with confirmation
    function deleteEmployee(employeeId) {
        if (confirm("Are you sure you want to delete this employee?")) {
            $.ajax({
                url: '/api/employee/' + employeeId,
                method: 'DELETE',
                success: function () {
                    alert('Employee deleted successfully.');
                    loadEmployees(currentPage, pageSize); // Reload employees after deletion
                },
                error: function () {
                    alert('Failed to delete employee.');
                }
            });
        }
    }

    // Event delegation for dynamically created buttons
    $('#employeeTable').on('click', '.edit-btn', function () {
        var employeeId = $(this).data('id');
        editEmployee(employeeId); // Implement this function
    });

    $('#employeeTable').on('click', '.delete-btn', function () {
        var employeeId = $(this).data('id');
        deleteEmployee(employeeId);
    });

    // Handle file input change to show image preview
    $('#editPhoto').on('change', function (event) {
        var file = event.target.files[0];  // Get the selected file
        if (file) {
            var reader = new FileReader();  // Create a FileReader object
            reader.onload = function (e) {
                // When the file is read, set the src of the image element to the file's data URL
                $('#editPhotoView').attr('src', e.target.result);
            };
            reader.readAsDataURL(file);  // Read the file as a data URL
        }
    });

    // Handle the edit form submission
    $('#editEmployeeForm').on('submit', function (event) {
        event.preventDefault(); // Prevent default form submission

        // Get form data
        var employeeId = $('#editEmployeeId').val();

        var formData = new FormData();
        formData.append('fullName', $('#editFullName').val());
        formData.append('email', $('#editEmail').val());
        formData.append('mobile', $('#editMobile').val());
        formData.append('dateOfBirth', $('#editDOB').val());

        var file = $('#editPhoto')[0].files[0];
        if (file) {
            formData.append('photo', file);
        }

        // Send AJAX request to update employee
        $.ajax({
            url: '/api/employee/' + employeeId,
            type: 'PUT',
            data: formData,
            processData: false, // Important: Do not process the data
            contentType: false, // Important: Set contentType to false to handle file upload
            success: function () {
                alert('Employee updated successfully.');
                $('#editEmployeeModal').modal('hide'); // Close the modal
                loadEmployees(currentPage, pageSize); // Reload the employee table
            },
            error: function () {
                alert('Failed to update employee.');
            }
        });
    });

    // Function to search employees
    function searchEmployees() {
        // ensuring encoding is correct
        var name = $('#searchName').val().trim();
        var dob = $('#searchDOB').val().trim();
        var email = $('#searchEmail').val().trim();
        var mobile = $('#searchMobile').val().trim();

        // Prepare an object to hold the query parameters
        var queryParams = {};

        // Add parameters to the query object if they are not empty
        if (name) {
            queryParams.name = encodeURIComponent(name);
        }
        if (dob) {
            queryParams.dateOfBirth = encodeURIComponent(dob);
        }
        if (email) {
            queryParams.email = encodeURIComponent(email);
        }
        if (mobile) {
            queryParams.mobile = encodeURIComponent(mobile);
        }
        // Convert queryParams object to a query string
        var queryString = $.param(queryParams);
        $.ajax({
            url: '/api/employee/search?' + queryString, // Ensure this endpoint supports POST requests
            method: 'GET',
            success: function (data) {
                var employeeTable = $('#employeeTable');
                employeeTable.empty(); // Clear previous content
                // Assuming data is an array of employee objects
                data.forEach(function (employee) {
                    loadTheEmployees(employee, employeeTable);
                });
            },
            error: function (xhr, status, error) {
                console.error('Failed to search employees:', error); // Log the error
                alert('Failed to search employees.');
            }
        });
    }

    // Bind the search button to the search function
    $('#searchButton').on('click', searchEmployees);

});

