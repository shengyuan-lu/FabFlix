let addStarForm = $("#add-star-form");
let addStarBtn = $("#add-star-btn");
let addStarMessage = $("#add-star-message");

/**
 * Handle the data returned by LoginServlet
 * @param resultData
 */
function handleAddStarResult(resultData) {
    // If login succeeds, it will redirect the user to index.html
    if (resultData["status"] === "success") {
        console.log("Adding star successful.");
        if (addStarMessage.hasClass("alert-danger")) {
            addStarMessage.removeClass("alert-danger");
        }
        if (!addStarMessage.hasClass("alert-success")) {
            addStarMessage.addClass("alert-success");
        }
        if (addStarMessage.hasClass("d-none")) {
            addStarMessage.removeClass("d-none");
        }

        addStarMessage.text(resultData["message"]);
    } else {
        console.log("Adding star failed.")
        if (addStarMessage.hasClass("alert-success")) {
            addStarMessage.removeClass("alert-success");
        }
        if (!addStarMessage.hasClass("alert-danger")) {
            addStarMessage.addClass("alert-danger");
        }
        if (addStarMessage.hasClass("d-none")) {
            addStarMessage.removeClass("d-none");
        }

        addStarMessage.text(resultData["message"]);
    }
}

/**
 * Submit the form content with POST method
 */
function addStar() {
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    // formSubmitEvent.preventDefault();
    // console.log(payment_form.serialize())
    $.ajax("api/add-star", {
        dataType: "json",
        method: "get",
        // Serialize the login form to the data sent by get request
        data: addStarForm.serialize(),
        success: (resultData) => handleAddStarResult(resultData),
    });
}

addStarBtn.on("click", addStar); // Bind the submit action of the form to a handler function