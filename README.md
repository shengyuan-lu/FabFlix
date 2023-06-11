## CS 122B Project 5: Scaling FabFlix and Performance Tuning

- # General
    - #### Team#: stanford_rejects

    - #### Names:
      - ***Team member 1: Tony Liu 34195333***  
      - ***Team member 2: Shengyuan Lu 93188958***

    - #### Project 5 Video Demo Link: [Click here for the demo video]()

    - #### Instruction of deployment:

    - #### Collaborations and Work Distribution:
      - Tony Liu
        - Task 2
        - Task 3
      - Shengyuan Lu
        - Task 1
        - Preserved recaptcha functionalities


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
      - Servlets
        - src/Servlets/ConfirmationServlet.java
        - src/Servlets/DashboardAddMovieServlet.java
        - src/Servlets/DashboardAddStarServlet.java
        - src/Servlets/DashboardLoginServlet.java
        - src/Servlets/DashboardMetadataServlet.java
        - src/Servlets/GenreServlet.java
        - src/Servlets/LoginServlet.java
        - src/Servlets/MovieListServlet.java
        - src/Servlets/MovieSuggestionServlet.java
        - src/Servlets/PaymentServlet.java
        - src/Servlets/ShoppingCartServlet.java
        - src/Servlets/SingleMovieServlet.java
        - src/Servlets/SingleStarServlet.java
      - Database Handler
        - src/Helpers/DatabaseHandler.java
      - Configuration Files
        - WebContent/META-INF/context.xml

    - #### Explain how Connection Pooling is utilized in the FabFlix code.
    When a servlet is initialized, we run `dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");` to get a data source which is registered in `context.xml`
    Because of our unique design, connection is only created in `DatabaseHandler.java`. In the servlet file, a database handler is initialized using `DatabaseHandler DBHandler = new DatabaseHandler(dataSource);`
    From the database handler, a connection is leased, and prepared statements created:
    `try (Connection conn = dataSource.getConnection())
    PreparedStatement preparedStatement = conn.prepareStatement(query);`
    The prepared statements are cached by set up `cachePrepStmts=true` in `context.xml`
    The "try with resources" approach will automatically close the connection, thus return it to the connection pool.

    - #### Explain how Connection Pooling works with two backend SQL.



- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    - #### How read/write requests were routed to Master/Slave SQL?


- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
    1. use `cd` command to change the directory where `log_processing.py` located
    2. make sure python is installed
    3. have the location of the log file ready
    4. run `python3 log_processing.py [location_of_log_file]`
    5. average TS and TJ results will be printed out


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**         | **Graph Results Screenshot**             | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis**                                                                                                                                                                                                                                                                                                                                                                                        |
|-----------------------------------------------|------------------------------------------|----------------------------|-------------------------------------|---------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Case 1: HTTP/1 thread                         | ![](img/Single-HTTP-1-Thread.jpg)        | 215                        | 79.86581738414635                   | 79.28440775406504         | This is a very standard case. We noticed that there's not much difference in TS and TJ. The entire tests run is the most smooth as there's not much stress on the server.                                                                                                                                                                                                                           |
| Case 2: HTTP/10 threads                       | ![](img/Single-HTTP-10-Thread.jpg)       | 895                        | 724.122489074248                    | 723.2768001917293         | We notice that with 10 threads, TS / TJ are about 10x compare to the test with a single thread / http. The difference between TS and TJ increased. Still a very smooth experience possibly due to connection pooling.                                                                                                                                                                               |
| Case 3: HTTPS/10 threads                      | ![](img/Single-HTTPS-10-Thread.jpg)      | 889                        | 611.4649128770116                   | 610.8658734793104         | We notice that the TS / TJ is smaller compare to the test with 10 threads / http, but the average query time stayed relatively the same. We looked up online and found https is generally slower than the http. Our test result contradicts this finding possibly because we restarted the server once before this test run, and it makes the server run faster. The test run is relatively smooth. |
| Case 4: HTTP/10 threads/No connection pooling | ![](img/Single-HTTP-10-Thread-No-CP.jpg) | 1402                       | 1202.374716446602                   | 1200.8125347262137        | Query time, TS / TJ all increased dramatically for about 50% compare to the test with connection pooling. The test run feels a bit laggy and the server become responsive for a while afterwards when we tried to retrieve the log. This indicates that connection pooling can significantly increase performance.                                                                                  |                                                                                                                                                                                       |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
