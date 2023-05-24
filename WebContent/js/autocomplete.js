/*

The Autocomplete is required to perform full-text search on movie title field.

The Autocomplete suggestion list should not have more than 10 entries.

[Finished] The Autocomplete should support keyboard navigation using ↑ ↓ arrow keys.

When a suggestion entry is selected, the entry should be highlighted, the text (query) in the search box should be changed to the entry's content (say, movie title).

Clicking on any of the suggestion entries, or pressing "Enter" Key if an entry is selected during keyboard navigation, it should jump to the corresponding Single Movie Page directly.

If the customer just presses "Enter" Key or clicks the search button without selecting any suggestion entry, it should perform the same full-text search action as stated above in the "full-text Search" requirement.

Only perform the Autocomplete search when the customer types in at least 3 (>= 3) characters.

[Finished] Set a small delay time (300ms) so that the frontend only performs the Autocomplete search after the customer stops typing for that delay.

Cache the suggestion list of each query (sent to the backend server) in the frontend, you can use LocalStorage or SessionStorage.

Whenever  Autocomplete search is triggered, check if the query and its suggestion list are in the cache. If not, send the query to the backend server to get a new suggestion list.

Make sure that each Autocomplete search finishes within 2s.

Print and only print log for the following cases:
    The Autocomplete search is initiated (after the delay);
    Whether the Autocomplete search is using cached results or sending an ajax request to the server;
    The used suggestion list (either from cache or server response).

 */


function handleLookup(query, doneCallback) {

    console.log("Autocomplete initiated")

    console.log("Sending AJAX request to backend Java Servlet")

    // TODO: if you want to check past query results first, you can do it here

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/movie-suggestion?query=" + escape(query),
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("Lookup ajax error")
            console.log(errorData)
        }
    })
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON

    console.log(data)

    // TODO: if you want to cache the result into a global variable you can do it here

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: data } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    console.log("You selected movie" + suggestion["value"] + " with ID " + suggestion["data"]["movieID"])
}


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
console.log($("#autocomplete"))
$('#autocomplete').autocomplete({

    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        console.log(query)
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },

    // set delay time
    deferRequestBy: 300,

    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});


/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})