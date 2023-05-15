## CS 122B Project 3:  reCAPTCHA, HTTPS, PreparedStatement, Stored Procedure, XML Parsing

***Team #: stanford_rejects***  
***Team member 1: Tony Liu 34195333***  
***Team member 2: Shengyuan Lu 93188958*** 

### Contributions

**Tony Liu**
- Task 2
- Task 3
- Task 5
- Task 6

**Shengyuan Lu**
- Task 1
- Task 3
- Task 4
- Task 6

### Filenames with Prepared Statements

- src/Helpers/DatabaseHandler.java (For FabFlix)
- src/xmlParser/helpers/XMLDatabaseHandler.java (For xml parser)
```
Notes to the grader:

For each part of the program, we designed a DatabaseHandler class to handle everything related to the query

This class has 2 methods:
executeQuery(String query, @Nullable Object... queryParameters)
executeUpdate(String query, @Nullable Object... queryParameters)

which prepares the query string with args queryStrings in this way:

    PreparedStatement preparedStatement = conn.prepareStatement(query);
    
    for (int i = 1; i <= queryParameters.length; ++i) {
        Object queryString = queryParameters[i-1];
        if (queryString instanceof Integer) {
            preparedStatement.setInt(i, (Integer) queryString);
        } else if (queryString instanceof String) {
            preparedStatement.setString(i, (String) queryString);
        } else if (queryString == null) {
            preparedStatement.setNull(i, Types.NULL);
        }
    }

Usage example:

    DatabaseHandler movieListDBHandler = new DatabaseHandler(dataSource);
    
    String movie_id = movie.get("id");
    
    String movieGenreQuery = "SELECT genres.id, genres.name FROM genres \n" +
            "JOIN genres_in_movies gim ON genres.id = gim.genreId\n" +
            "WHERE gim.movieId = ?\n" +
            "ORDER BY genres.name\n" +
            "LIMIT 3\n";
    
    List<HashMap<String, String>> genres = movieListDBHandler.executeQuery(movieGenreQuery, movie_id);

```
### Two parsing time optimization strategies
- We frequently used HashMap and HashSet to reduce retrieval time. For example, with
```private HashMap<String, Movie> parsedMovies; // Key = Movie ID, Value = Movie Object```, we can identify a movie by ID and later when we want to insert a star into a movie, we can find a movie in O(1) time complexity by ID. 
- Instead of updating the database row by row, we create CSV files to contain all new rows we want to add, and load it to the database at one. This approach makes parsing significantly faster.

### Inconsistent data reports
The parser will generate the following inconsistency files
- CastInconsistencyReport.txt
- MovieInconsistencyReport.txt
- StarInconsistencyReport.txt

Due to the massive size of the file, it is not possible to copy all contents here, but we can show you examples of what is logged:

Example In CastInconsistencyReport.txt:

``Star Tony Wright from actors63.xml does not exists in casts124.xml.``

``Movie ID p-SeL40 from casts124.xml does not exists in mains243.xml.``

Example In MovieInconsistencyReport.txt:

``Movie with ID p-FB39 has no star.``

``Movie Parsing Failed Because Of Missing Required Field(s): ID: null``

Example In StarInconsistencyReport:

``Star Wilford Brimley already existed in the database.``

### Demo Video
[Click here for the demo video]()
