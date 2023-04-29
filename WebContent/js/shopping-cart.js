/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function getStarsHtml(starsList) {
    let starsHTML = "<ul>";
    for (let i = 0; i < starsList.length; i++) {
        starsHTML += `<li><a href="single-star.html?id=${starsList[i]["id"]}">${starsList[i]["name"]}</a></li>`;
    }
    starsHTML += "</ul>";
    return starsHTML;
}

function getGenresHtml(genresList) {
    let genresHTML = "<ul>";
    for (let i = 0; i < genresList.length; i++) {
        genresHTML += `<li>${genresList[i]["name"]}</li>`;
    }
    genresHTML += "</ul>";
    return genresHTML;
}

/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;

    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return "";

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleShoppingCartResult(resultData) {
    console.log(`handleStarResult: ${resultData}`);

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let shoppingCartTableElement = jQuery("#shopping-cart-table");

    for (let i = 0; i < resultData.length; i++) {
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += `<th>
            <a href="single-movie.html?id=${resultData[i]["movie_id"]}"><h3>${resultData[i]["movie_title"]}</h3>
            </a> 
           </th>`;
        rowHTML += `<th class="fs-4">${resultData[i]["movie_quantity"]}</th>`;
        rowHTML += `<th class="fs-4">${resultData[i]["movie_price"]}</th>`;
        rowHTML += `<th class="fs-4">${resultData[i]["movie_total"]}</th>`;
        rowHTML += `<th class="fs-4"><button class="btn btn-outline-primary" id="${resultData[i]['movie_id']}" onclick="removeMovieFromCart(this.id)">Remove from Cart</button></th>`
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        shoppingCartTableElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handlemovieResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/shopping-cart", // Setting request url
    success: (resultData) => handleShoppingCartResult(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
});
