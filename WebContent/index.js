/**
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function getStarsHtml(starsList) {
    let starsHTML = "<ul>";
    for (let i = 0; i < starsList.length; i++) {
        starsHTML += `<li><a href="single-star.html?id=${starsList[i]["id"]}">${starsList[i]["name"]}</a></li>`;

    }
    starsHTML += "</ul>"
    return starsHTML;
}

function getGenresHtml(genresList) {
    let starsHTML = "<ul>";
    for (let i = 0; i < genresList.length; i++) {
        starsHTML += `<li>${genresList[i]["name"]}</li>`;

    }
    starsHTML += "</ul>"
    return starsHTML;
}

function starIcon(fill = "#facc69") {
    return `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="${fill}" class="bi bi-star-fill" viewBox="0 0 16 16">
  <path d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"/>
</svg>`
}
function handleMovieResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += `<th>
            <a href="single-movie.html?id=${resultData[i]['movie_id']}"><h3>${resultData[i]["movie_title"]}</h3>
            </a> 
           </th>`;
        rowHTML += `<th class="fs-4">${resultData[i]["movie_year"]}</th>`
        rowHTML += `<th class="fs-4">${resultData[i]["movie_director"]}</th>`
        rowHTML += `<th class="fs-4">${getGenresHtml(resultData[i]["movie_genres"])}</th>`
        rowHTML += `<th class="fs-4">${getStarsHtml(resultData[i]["movie_stars"])}</th>`
        rowHTML += `<th class="fs-3"><div class="d-flex flex-row align-items-center"><span class="me-2">${starIcon()}</span>${resultData[i]["movie_rating"]}</div></th>`;
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handlemovieResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by moviesServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});