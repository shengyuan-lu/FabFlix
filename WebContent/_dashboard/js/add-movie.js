let addMovieForm = $("#add-movie-form");
let addMovieBtn = $("#add-movie-btn");
let addMovieMessage = $("#add-movie-message");

/**
 * Handle the data returned by LoginServlet
 * @param resultData
 */
function handleAddMovieResult(resultData) {
    // If login succeeds, it will redirect the user to index.html
    if (resultData["status"] === "success") {
        console.log("Adding movie successful.");
        if (addMovieMessage.hasClass("alert-danger")) {
            addMovieMessage.removeClass("alert-danger");
        }
        if (!addMovieMessage.hasClass("alert-success")) {
            addMovieMessage.addClass("alert-success");
        }
        if (addMovieMessage.hasClass("d-none")) {
            addMovieMessage.removeClass("d-none");
        }

        addMovieMessage.text(resultData["message"]);
    } else {
        console.log("Adding movie failed.")
        if (addMovieMessage.hasClass("alert-success")) {
            addMovieMessage.removeClass("alert-success");
        }
        if (!addMovieMessage.hasClass("alert-danger")) {
            addMovieMessage.addClass("alert-danger");
        }
        if (addMovieMessage.hasClass("d-none")) {
            addMovieMessage.removeClass("d-none");
        }

        addMovieMessage.text(resultData["message"]);
    }
}

/**
 * Submit the form content with POST method
 */
function addMovie() {
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    // formSubmitEvent.preventDefault();
    // console.log(payment_form.serialize())
    $.ajax("api/add-movie", {
        dataType: "json",
        method: "POST",
        // Serialize the login form to the data sent by POST request
        data: addMovieForm.serialize(),
        success: (resultData) => handleAddMovieResult(resultData),
    });
}

addMovieBtn.on("click", addMovie); // Bind the submit action of the form to a handler function