## CS 122B Project 3:  reCAPTCHA, HTTPS, PreparedStatement, Stored Procedure, XML Parsing

***Team #: stanford_rejects***  
***Team member 1: Tony Liu 34195333***  
***Team member 2: Shengyuan Lu 93188958*** 

### Contributions

**Tony Liu**


**Shengyuan Lu**

### Filenames with Prepared Statements

- src/Helpers/DatabaseHandler.java
```
Notes to the grader:

We designed a DatabaseHandler class to handle everything related to the query

This class has 2 methods:
executeQuery(String query, String... queryStrings)
executeUpdate(String query, String... queryStrings)

which prepares the query string with args queryStrings in this way:

    PreparedStatement preparedStatement = conn.prepareStatement(query);
    
    for (int i = 1; i <= queryStrings.length; ++i) {
        preparedStatement.setString(i, queryStrings[i - 1]);
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

### Demo Video
[Click here for the demo video]()
