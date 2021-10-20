DO $$ BEGIN
    CREATE TYPE trip_type AS ENUM ('START','STOP','ACTIVE' );
    CREATE CAST (character varying AS trip_type) WITH INOUT AS ASSIGNMENT;
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS trip (
    id SERIAL PRIMARY KEY,
    car_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    state trip_type NOT NULL
);

CREATE TABLE IF NOT EXISTS driver (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS car (
    id SERIAL PRIMARY KEY,
    model TEXT NOT NULL,
    plate TEXT NOT NULL
);