import {plusIcon, minusIcon} from "./icons.js"

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
        rowHTML += `<th class="fs-4"><button class="btn" name="${resultData[i]['movie_id']}" onclick="addOneMovieToCart(this.name)">${plusIcon()}</button><span id="${resultData[i]['movie_id']}">${resultData[i]["movie_quantity"]}</span><button class="btn" name="${resultData[i]['movie_id']}" onclick="removeOneMovieFromCart(this.name)">${minusIcon()}</button></th>`;
        rowHTML += `<th class="fs-4">${resultData[i]["movie_price"]}</th>`;
        rowHTML += `<th class="fs-4"><i class="bi bi-file-plus"></i>${resultData[i]["movie_total"]}</th>`;
        rowHTML += `<th class="fs-4"><button class="btn btn-outline-primary" name="${resultData[i]['movie_id']}" onclick="removeMovieFromCart(this.name)">Remove from Cart</button></th>`
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
