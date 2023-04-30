import { starIcon } from "./icons.js";

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

function getGenresHtml(genresList) {
    let genresHTML = "<ul>";
    for (let i = 0; i < genresList.length; ++i) {
        genresHTML += `<li>${genresList[i]}</li>`;
    }
    genresHTML += "</ul>";
    return genresHTML;
}

function getStarsHtml(starsList) {
    console.log(starsList);
    let starsHTML = "<ul>";
    for (let i = 0; i < starsList.length; ++i) {
        starsHTML += `<li><a href="single-star.html?id=${starsList[i]["starId"]}">${starsList[i]["starName"]}</a></li>`;
    }
    starsHTML += "</ul>";
    return starsHTML;
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {
    console.log("handleResult: populating movie info from resultData");

    // Populate the single movie table
    // Find the empty table body by id "movie_table_body"
    let addToCartBtn = jQuery("#add-to-cart-btn");
    let movieNameElem = jQuery(".movie-title");
    let movieReleaseYearElem = jQuery(".movie-release-year");
    let movieDirectorElem = jQuery(".movie-director");
    let movieGenresElem = jQuery(".movie-genres");
    let movieStarsElem = jQuery(".movie-stars");
    let movieRatingElem = jQuery(".movie-rating");

    addToCartBtn.html(`<button class="btn btn-outline-primary" name="${resultData['movieId']}" onclick="addMovieToCart(this.name)">Add to Cart</button>`);
    movieNameElem.html(resultData["movieTitle"]);
    movieReleaseYearElem.html(resultData["movieYear"]);
    movieDirectorElem.html(resultData["movieDirector"]);
    movieGenresElem.html(getGenresHtml(resultData["movieGenres"]));
    movieStarsElem.html(getStarsHtml(resultData["movieStars"]));
    movieRatingElem.html(
        `<div class="d-flex flex-row align-items-center"><span class="me-2">${starIcon()}</span>${
            resultData["movieRating"]
        }</div>`
    );

    // Append the row created to the table body, which will refresh the page
    // movieTableBodyElement.append(rowHTML);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName("id");

jQuery("#back-link").attr("href", sessionStorage.getItem("prevUrl"));

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData), // Setting callback function to handle data returned successfully by the SingleStarServlet
});
