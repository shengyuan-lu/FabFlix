## CS 122B Project 2: Developing FabFlix Website

***Team #: stanford_rejects***  
***Team member 1: Tony Liu 34195333***  
***Team member 2: Shengyuan Lu 93188958*** 

### Contributions

**Tony Liu**
- Implemented shopping cart page, which displays information about movies in the shopping cart and allows for quantity modification for each item as well as deletion of items in the shopping cart 
- Constructed payment page, including card information collection and verification
- Built Confirmation page, displaying information about the movies ordered
- Added "add to shopping cart" button on movie list and single movie page
- Refactored movies and sales tables in the MySQL database
- Helped with video demo and AWS setup

**Shengyuan Lu**
- Built login functionalities
- Built search and browse functionalities
- Designed substring match patterns
- Built sorting, display # of results functionalities
- Maintained the status of the Movie List Page
- Helped with video demo and AWS setup

### Substring Matching Design

Search

- Title: `LIKE %ABC%`
- Director: `LIKE %ABC%`
- Year: Exact match only
- Star: `LIKE %ABC%`

Browse

- Alphabet (NOT *): `WHERE LOWER(title) LIKE LOWER(A%)` 
- Alphabet (* Only): `WHERE title regexp ^[^A-Za-z0-9]`
- Genre: Exact match by genre id only

### Demo Video
[Click here for the demo video](https://youtu.be/Uv-Rdf61szo)
