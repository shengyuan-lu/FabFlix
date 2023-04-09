CREATE DATABASE IF NOT EXISTS moviedb;

USE moviedb;

CREATE TABLE IF NOT EXISTS movies (
  id varchar(10) NOT NULL,
  title varchar(100) NOT NULL,
  year integer NOT NULL,
  director varchar(100) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS stars (
  id varchar(10) NOT NULL,
  name varchar(100) NOT NULL,
  birthYear integer,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS stars_in_movies (
  starId varchar(10) NOT NULL,
  movieId varchar(10) NOT NULL,
  FOREIGN KEY (starId) REFERENCES stars(id),
  FOREIGN KEY (movieID) REFERENCES movies(id)
);

CREATE TABLE IF NOT EXISTS genres (
  id integer AUTO_INCREMENT NOT NULL,
  name varchar(32) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS genres_in_movies (
  genreId integer NOT NULL,
  movieId varchar(10) NOT NULL,
  FOREIGN KEY (genreId) REFERENCES genres(id),
  FOREIGN KEY (movieID) REFERENCES movies(id)
);

CREATE TABLE IF NOT EXISTS creditcards (
  id varchar(20) NOT NULL,
  firstName varchar(50) NOT NULL,
  lastName varchar(50) NOT NULL,
  expiration date NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS customers (
  id integer AUTO_INCREMENT NOT NULL,
  firstName varchar(50) NOT NULL,
  lastName varchar(50) NOT NULL,
  ccId varchar(20) NOT NULL,
  address varchar(200) NOT NULL,
  email varchar(50) NOT NULL,
  password varchar(20) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (ccID) REFERENCES creditcards(id)
);

CREATE TABLE IF NOT EXISTS sales (
  id integer AUTO_INCREMENT NOT NULL,
  customerId integer NOT NULL,
  movieId varchar(10) NOT NULL,
  saleDate date NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (customerID) REFERENCES customers(id),
  FOREIGN KEY (movieID) REFERENCES movies(id)
);

CREATE TABLE IF NOT EXISTS ratings (
  movieId varchar(10) NOT NULL,
  rating float NOT NULL,
  numVotes integer NOT NULL,
  FOREIGN KEY (movieID) REFERENCES movies(id)
);