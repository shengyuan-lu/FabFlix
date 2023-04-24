function handleGenreResult(resultData) {
    let genreList = jQuery("#genre_list");
    let listHTML = "";
    for (let i = 0; i < resultData.length; i++) {
        listHTML += `<li class="list-group-item col-4 col-sm-2 pt-2 pb-2"><a class="text-decoration-none" href="movie-list.html?genre_id=${resultData[i]["genre_id"]}">
${resultData[i]["genre_name"]}
</a></li>`;
    }
    genreList.append(listHTML);
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/genre", // Setting request url, which is mapped by moviesServlet in Stars.java
    success: (resultData) => handleGenreResult(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
});
