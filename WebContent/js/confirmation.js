import {starIcon} from "./icons";

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
function handleConfirmationResult(resultData, limit) {
    console.log(
        "handleConfirmationResult: populating movie list table from resultData"
    );

    if (resultData.length < limit) {
        jQuery("#next-btn").hide();
    }

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    for (let i = 0; i < resultData.length; i++) {
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += `<th>
            <a href="single-movie.html?id=${resultData[i]["movie_id"]}"><h3>${resultData[i]["movie_title"]}</h3>
            </a> 
           </th>`;
        rowHTML += `<th class="fs-4">${resultData[i]["movie_year"]}</th>`;
        rowHTML += `<th class="fs-4">${resultData[i]["movie_director"]}</th>`;
        rowHTML += `<th class="fs-4">${getGenresHtml(
            resultData[i]["movie_genres"]
        )}</th>`;
        rowHTML += `<th class="fs-4">${getStarsHtml(
            resultData[i]["movie_stars"]
        )}</th>`;
        rowHTML += `<th class="fs-3"><div class="d-flex flex-row align-items-center"><span class="me-1 d-flex">${starIcon(
            { size: 20 }
        )}</span>${resultData[i]["movie_rating"]}</div></th>`;
        rowHTML += `<th class="fs-4"><button class="btn btn-outline-primary" id="${resultData[i]["movie_id"]}" onclick="addMovieToCart(this.id)">Add to Cart</button></th>`;
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

// Bind the submit action of the form to a handler function
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "/api/confirmation", // Setting request url, which is mapped by moviesServlet in Stars.java
    success: (resultData) => handleConfirmationResult(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
});

