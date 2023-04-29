let paymentForm = $("#payment-form");
let placeOrderBtn = $("#place-order-btn");
let paymentErrorMessage = $("#payment-error-message");

/**
 * Handle the data returned by LoginServlet
 * @param resultData
 */
function handlePlaceOrderResult(resultData) {
    // If login succeeds, it will redirect the user to index.html
    if (resultData["status"] === "success") {
        console.log("Payment status is success.");
        window.location.replace("confirmation.html");
    } else {
        console.log("Payment status is fail.");
        // If user enters wrong credit card information, payment page will display error message
        paymentErrorMessage.text(resultData["message"]);
        paymentErrorMessage.removeClass("d-none");
    }
}

/**
 * Submit the form content with POST method
 */
function placeOrder() {
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    // formSubmitEvent.preventDefault();
    // console.log(payment_form.serialize())
    $.ajax("api/payment", {
        dataType: "json",
        method: "POST",
        // Serialize the login form to the data sent by POST request
        data: paymentForm.serialize(),
        success: (resultData) => handlePlaceOrderResult(resultData),
    });
}

// Bind the submit action of the form to a handler function
placeOrderBtn.on("click", placeOrder);
