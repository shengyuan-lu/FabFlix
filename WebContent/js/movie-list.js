import { starIcon } from "./icons.js";

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

function handleMovieResult(resultData, limit) {
    console.log(
        "handleStarResult: populating movie list table from resultData"
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
        rowHTML += `<th class="fs-3"><div class="d-flex flex-row align-items-center"><span class="me-2">${starIcon()}</span>${
            resultData[i]["movie_rating"]
        }</div></th>`;
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

function assembleRequestURL(baseUrl = "api/movies", offsetVal = offset) {
    let requestUrl = baseUrl;

    let title = getParameterByName("title");
    let director_name = getParameterByName("director_name");
    let year = getParameterByName("year");
    let star_name = getParameterByName("star_name");

    let alphabet = getParameterByName("alphabet");
    let genre_id = getParameterByName("genre_id");

    let total_results = parseInt(getParameterByName("total_results"));

    // !!string tests if the string is null or empty

    // Add ? to signal the beginning of query string
    if (
        !!alphabet ||
        !!title ||
        !!director_name ||
        !!year ||
        !!genre_id ||
        !!star_name ||
        !!offset ||
        !!limit
    ) {
        requestUrl += "?";
    }

    // Append the query parameters
    if (!!alphabet) {
        requestUrl += `alphabet=${alphabet}&`;
    }

    if (!!title) {
        requestUrl += `title=${title}&`;
    }

    if (!!director_name) {
        requestUrl += `director_name=${director_name}&`;
    }

    if (!!year) {
        requestUrl += `year=${year}&`;
    }

    if (!!genre_id) {
        requestUrl += `genre_id=${genre_id}&`;
    }

    if (!!star_name) {
        requestUrl += `star_name=${star_name}&`;
    }

    // config offset and limit

    requestUrl += `offset=${offsetVal}&`;

    requestUrl += `limit=${limit}&`;

    // Trim the last &
    if (requestUrl.endsWith("&")) {
        requestUrl = requestUrl.substring(0, requestUrl.length - 1);
    }

    return requestUrl;
}

let prevBtn = jQuery("#prev-btn");
let nextBtn = jQuery("#next-btn");
let limitSelector = jQuery("#limit-selector");

// current offset and limit
let offset = parseInt(getParameterByName("offset")) || 0;
let limit = parseInt(getParameterByName("limit")) || 10;

switch (limit) {
    case 10:
        limitSelector.val("10");
        break;
    case 25:
        limitSelector.val("25");
        break;
    case 50:
        limitSelector.val("50");
        break;
    case 100:
        limitSelector.val("100");
        break;
    default:
        limitSelector.val("10");
        break;
}

if (offset === 0) {
    prevBtn.remove();
} else {
    prevBtn.attr(
        "href",
        assembleRequestURL("movie-list.html", Math.max(offset - limit, 0))
    );
}

nextBtn.attr("href", assembleRequestURL("movie-list.html", offset + limit));
limitSelector.on("change", (e) => {
    limit = e.target.value;
    console.log(limit);
    window.location.href = assembleRequestURL("movie-list.html");
});

// Makes the HTTP GET request and registers on success callback function handleMovieResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: assembleRequestURL(), // Setting request url, which is mapped by moviesServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData, limit), // Setting callback function to handle data returned successfully by the StarsServlet
});
