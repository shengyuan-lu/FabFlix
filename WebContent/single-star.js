/**
 * This project is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


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
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    let starName = jQuery(".star-name");
    let starDOB = jQuery(".star-dob");
    let starMovies = jQuery(".star-movies")
    starName.html(resultData["star_name"])

    if (resultData["star_dob"] != null) {
        starDOB.html(resultData["star_dob"])
    } else {
        starDOB.html("N/A")
    }


    console.log(`handleResult: populating star with id ${starId} info from resultData`);

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieListHTML = "<ul>"

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, resultData["movies"].length); i++) {
        let movies = resultData["movies"][i]
        let liHTML = `<li><a href="single-movie.html?id=${movies["movie_id"]}">${movies["movie_title"]}</a></li>`;

        // Append the row created to the table body, which will refresh the page
        movieListHTML+=liHTML
    }
    movieListHTML += "</ul>"
    starMovies.html(movieListHTML)
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});