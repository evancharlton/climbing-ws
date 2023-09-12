-- CREATE DATABASE buldreinfo IF NOT EXISTS;

USE buldreinfo;
SET autocommit=0; SOURCE ./20230728-100208.buldreinfo.no-data.sql; COMMIT;
-- TODO Remove the following ALTER TABLE-lines when database (20230728-100208.buldreinfo.no-data.sql) is updated.
ALTER TABLE `buldreinfo`.`problem` ADD COLUMN `broken` VARCHAR(255) AFTER `sector_id`;
ALTER TABLE `buldreinfo`.`area` ADD COLUMN `sun_from_hour` INT NULL AFTER `no_dogs_allowed`, ADD COLUMN `sun_to_hour` INT NULL AFTER `sun_from_hour`;
ALTER TABLE `buldreinfo`.`sector` ADD COLUMN `wall_direction` VARCHAR(32) NULL AFTER `polygon_coords`;
DELETE FROM region WHERE 1;
INSERT INTO region (id, name, title, description, url, polygon_coords, latitude, longitude, default_zoom, emails) VALUES (1, 'Dev1', 'Title', 'Description', 'http://localhost', '58.95852920349744,5.43548583984375;59.139339347998906,5.54534912109375;59.32900841886421,5.990295410156251;59.38780167734329,6.517639160156251;59.139339347998906,7.028503417968751;58.991785092994974,7.033996582031251;58.59547775958452,6.8499755859375;58.26619900311628,6.896667480468751;58.16927656729275,6.594543457031251;58.467870587058236,5.77606201171875;58.729750254584566,5.457458496093751', 58.72, 6.62, 8, null);
INSERT INTO type VALUES (1, 'Climbing', 'Route', 'Bolt');
INSERT INTO type  VALUES (2, 'Climbing', 'Route', 'Trad');
INSERT INTO region_type (region_id, type_id) VALUES (1, 1);
INSERT INTO region_type (region_id, type_id) VALUES (1, 2);
INSERT INTO grade VALUES ('CLIMBING', 0, 'n/a', 0, 'n/a');
INSERT INTO grade VALUES ('CLIMBING', 45, '8 (7b+)', 4, 8);
COMMIT;