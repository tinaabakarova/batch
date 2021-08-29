insert into authors (author_id, author_name) values(1, 'Pushkin A.S');
insert into authors (author_id, author_name) values(2, 'unit testovich');
insert into genres (genre_id, genre_name) values (1, 'poem');
insert into genres (genre_id, genre_name) values (2, 'comedy');
insert into books (id, name, author, genre) values (1, 'Ruslan and Ludmila', 1, 1);
insert into books (id, name, author, genre) values (2, 'King Saltan fairytale', 1, 1);
insert into comments (id, comment, book, user_name) values (1, 'very interesting book!', 1, 'user12345');
insert into comments (id, comment, book, user_name) values (2, 'great!', 1, 'user1234');