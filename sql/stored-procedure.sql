use moviedb;

-- Change DELIMITER to $$
delimiter $$

create procedure add_movie (
    in movie_title varchar(100),
    in movie_year integer,
    in movie_director varchar(100),
    in star_name varchar(100),
    in star_birth_year integer,
    in genre_name varchar(32)
)
begin
    declare max_movie_id varchar(10);
    declare max_movie_id_prefix char(2);
    declare max_movie_id_number integer; # Two-letter prefix for the movie id
    declare next_movie_id varchar(10);
    
    declare star_id varchar(10);
    declare max_star_id varchar(10);
    declare max_star_id_prefix char(2);
    declare max_star_id_number integer; # Two-letter prefix for the star id
    declare next_star_id varchar(10);
    declare is_star_in_movie_exists bool;
    
    declare genre_id integer;
    declare max_genre_id integer;
    declare next_genre_id varchar(10);
    declare is_genre_in_movie_exists bool;

	# Create the new movie record
	select max(id) into max_movie_id from movies;
	set max_movie_id_prefix = substring(max_movie_id, 1, 2); 
	set max_movie_id_number = cast(substring(max_movie_id, 3, 7) as unsigned);
	set next_movie_id = concat(max_movie_id_prefix, cast((max_movie_id_number + 1) as char)); # Increment movie id

	insert into movies (id, title, year, director, price)
	values (next_movie_id, movie_title, movie_year, movie_director, ROUND(RAND() * 9 + 1, 2));
	

	# =========== Add star for a movie ==========
	select id into star_id from stars
	where name = star_name
	limit 1;

	if (star_id is null) then
		# If star doesn't exist yet, create it
		select max(id) into max_star_id from movies;
		set max_star_id_prefix = substring(max_star_id, 1, 2); 
		set max_star_id_number = cast(substring(max_star_id, 3, 7) as unsigned);
		set next_star_id = concat(max_star_id_prefix, cast((max_star_id_number + 1) as char)); # Increment movie id

		insert into stars (id, name, birthyear)
		values (next_star_id, star_name, star_birth_year);
	else
		# If star already exist in the database, retrieve star id
		select max(id) into next_star_id from stars
		where stars.name = star_name;
	end if;
	
	set is_star_in_movie_exists = exists(SELECT * from stars_in_movies as sim WHERE sim.starsId= next_star_id and sim.movieId = next_movie_id);
	if (not is_star_in_movie_exists) then
		insert into stars_in_movies (starId, movieId)
		values (next_star_id, next_movie_id);
	end if;
	
	
	# =========== Add genre for a movie ==========
	select id into genre_id from genres
	where name = genre_name
	limit 1;

	if (genre_id is null) then
		# If the genre doesn't exist yet, create it
		select max(id) into max_genre_id from genres;
		set next_genre_id = max_genre_id + 1;
		
		insert into genres (id, name)
		values (next_genre_id, genre_name);
	else
		# If the genre already exists in the database, retreive genre id
		select id into next_genre_id from genres
		where genres.name = genre_name;
	end if;
	
	set is_genre_in_movie_exists = EXISTS(SELECT * from genres_in_movies as gim WHERE gim.genreId = next_genre_id and gim.movieId = next_movie_id);
	if (not is_genre_in_movie_exists) then
		insert into genres_in_movies (genreId, movieId)
		values (next_genre_id, next_movie_id);
	end if;
end
$$

-- Change back DELIMITER to ;
DELIMITER ;
