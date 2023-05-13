let dashboardMetadataTable = $("#dashboard-metadata-table");

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMetadataResult(resultData) {
    let metadataTableHTML = "";
    for (const [tableName, tableInfo] of Object.entries(resultData)) {
        metadataTableHTML += `<h3>${tableName}<h3>
                                <table class="table table-striped mt-4 border">
                                <thead class="table-dark fs-5">
                                <tr>
                                    <th>Attribute</th>
                                    <th>Type</th>
                                </tr>
                                </thead>
                                <tbody>`;
        for (const [columnName, columnType] of Object.entries(tableInfo)) {
            metadataTableHTML += "<tr>";
            metadataTableHTML += `<td><h3>${columnName}</h3></td>`;
            metadataTableHTML += `<td><h3>${columnType}</h3></td>`;
            metadataTableHTML += "</tr>";
        }

        metadataTableHTML += `</tbody>
                                </table>`;
    }

    dashboardMetadataTable.html(metadataTableHTML);
}

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/dashboard-metadata",
    success: (resultData) => handleMetadataResult(resultData),
});
