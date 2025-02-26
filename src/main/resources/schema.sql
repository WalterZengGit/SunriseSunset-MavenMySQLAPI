CREATE TABLE IF NOT EXISTS sunset_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date VARCHAR(20),
    latitude DOUBLE,
    longitude DOUBLE,
    sunrise DATETIME,
    sunset DATETIME
);