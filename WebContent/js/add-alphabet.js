function handleAlphabetResult(resultData) {
    let alphabetList = jQuery("#alphabet_list");
    let listHTML = "";
    for (let i = 0; i < resultData.length; i++) {
        listHTML += `<li class="list-group-item col-1 pt-1 pb-1"><a class="text-decoration-none" href="movie-list.html?alphabet=${resultData[i]}">
${resultData[i]}
</a></li>`;
    }
    alphabetList.append(listHTML);
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/alphabet", // Setting request url, which is mapped by moviesServlet in Stars.java
    success: (resultData) => handleAlphabetResult(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
});