CREATE TABLE IF NOT EXISTS penalty (
    id SERIAL PRIMARY KEY,
    driver_id BIGINT NOT NULL,
    total_points BIGINT NOT NULL
);