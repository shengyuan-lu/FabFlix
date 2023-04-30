/**
 * Submit the form content with POST method
 */
function handleConfirmationResult(resultData, limit) {
    console.log(
        "handleConfirmationResult: populating order confirmation table from resultData"
    );

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let orderConfirmationTableBodyElement = jQuery(
        "#order-confirmation-table-body"
    );

    for (let i = 0; i < resultData.length; i++) {
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += `<th class="fs-4">${resultData[i]["saleId"]}</th>`;
        rowHTML += `<th class="fs-4">${resultData[i]["movieTitle"]}</th>`;
        rowHTML += `<th class="fs-4">${resultData[i]["saleQuantity"]}</th>`;
        rowHTML += `<th class="fs-4">${resultData[i]["moviePrice"]}</th>`;
        rowHTML += `<th class="fs-4">${
            parseFloat(resultData[i]["moviePrice"]) *
            parseInt(resultData[i]["saleQuantity"])
        }</th>`;
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        orderConfirmationTableBodyElement.append(rowHTML);
    }
}

// Bind the submit action of the form to a handler function
$.ajax("api/confirmation", {
    dataType: "json", // Setting return data type
    method: "POST", // Setting request method
    success: (resultData) => handleConfirmationResult(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
});
