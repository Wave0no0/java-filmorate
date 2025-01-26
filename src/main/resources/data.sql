UPDATE genres SET name = 'Мультфильм' WHERE id = 3;
UPDATE genres SET name = 'Триллер' WHERE id = 4;
UPDATE genres SET name = 'Документальный' WHERE id = 5;
UPDATE genres SET name = 'Боевик' WHERE id = 6;

MERGE INTO genres (id, name) KEY(id) VALUES (1, 'Комедия');
MERGE INTO genres (id, name) KEY(id) VALUES (2, 'Драма');
MERGE INTO genres (id, name) KEY(id) VALUES (3, 'Мультфильм');
MERGE INTO genres (id, name) KEY(id) VALUES (4, 'Триллер');
MERGE INTO genres (id, name) KEY(id) VALUES (5, 'Документальный');
MERGE INTO genres (id, name) KEY(id) VALUES (6, 'Боевик');

MERGE INTO ratings (id, name) KEY(id) VALUES (1, 'G');
MERGE INTO ratings (id, name) KEY(id) VALUES (2, 'PG');
MERGE INTO ratings (id, name) KEY(id) VALUES (3, 'PG-13');
MERGE INTO ratings (id, name) KEY(id) VALUES (4, 'R');
MERGE INTO ratings (id, name) KEY(id) VALUES (5, 'NC-17');