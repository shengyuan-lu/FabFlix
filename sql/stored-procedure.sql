use moviedb;

drop procedure if exists add_movie;

-- Change DELIMITER to $$
delimiter $$

create procedure add_movie (
    in movie_title varchar(100),
    in movie_year integer,
    in movie_director varchar(100),
    in star_name varchar(100),
    in star_birth_year integer,
    in genre_name varchar(32),
    out is_movie_exists bool,
    out new_movie_id varchar(10),
    out new_star_id varchar(10),
    out new_genre_id varchar(10)
)
begin
    declare max_movie_id varchar(10);
    declare max_movie_id_number integer;
    
    declare is_star_exists bool;
    declare max_star_id varchar(10);
    declare max_star_id_number integer;
    declare is_star_in_movie_exists bool;
    
    declare is_genre_exists bool;
    declare max_genre_id integer;
    declare is_genre_in_movie_exists bool;

	set is_movie_exists = exists(select * from movies where movies.title = movie_title and movies.year = movie_year and movies.director = movie_director);
	if not is_movie_exists then
		# If the movie doesn't exist yet, create it
		select max(id) into max_movie_id from movies;
		set max_movie_id_number = cast(substring(max_movie_id, 3, 7) as unsigned);
		set new_movie_id = concat("tt", cast((max_movie_id_number + 1) as char)); # Increment movie id

		insert into movies (id, title, year, director, price)
		values (new_movie_id, movie_title, movie_year, movie_director, ROUND(RAND() * 9 + 1, 2));
		

		# =========== Add star for a movie ==========
		set is_star_exists = exists(select * from stars where stars.name = star_name);
		if not is_star_exists then
			# If star doesn't exist yet, create it
			select max(id) into max_star_id from stars;
			set max_star_id_number = cast(substring(max_star_id, 3, 7) as unsigned);
			set new_star_id = concat("nm", cast((max_star_id_number + 1) as char)); # Increment star id

			insert into stars (id, name, birthyear)
			values (new_star_id, star_name, star_birth_year);
		else
			# If star already exist in the database, retrieve star id
			select max(id) into new_star_id from stars
			where stars.name = star_name;
		end if;
		
		set is_star_in_movie_exists = exists(SELECT * from stars_in_movies as sim WHERE sim.starId= new_star_id and sim.movieId = new_movie_id);
		if (not is_star_in_movie_exists) then
			insert into stars_in_movies (starId, movieId)
			values (new_star_id, new_movie_id);
		end if;
		
		
		# =========== Add genre for a movie ==========
		set is_genre_exists = exists(select * from genres where genres.name = genre_name);
		if not is_genre_exists then
			# If the genre doesn't exist yet, create it
			select max(id) into max_genre_id from genres;
			set new_genre_id = max_genre_id + 1;
			
			insert into genres (id, name)
			values (new_genre_id, genre_name);
		else
			# If the genre already exists in the database, retreive genre id
			select id into new_genre_id from genres
			where genres.name = genre_name;
		end if;
		
		set is_genre_in_movie_exists = EXISTS(SELECT * from genres_in_movies as gim WHERE gim.genreId = new_genre_id and gim.movieId = new_movie_id);
		if (not is_genre_in_movie_exists) then
			insert into genres_in_movies (genreId, movieId)
			values (new_genre_id, new_movie_id);
		end if;
	end if;
end
$$

-- Change back DELIMITER to ;
DELIMITER ;
