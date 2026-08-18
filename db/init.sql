CREATE SCHEMA IF NOT EXISTS public;
CREATE SCHEMA IF NOT EXISTS acme;

CREATE TABLE IF NOT EXISTS public.users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS acme.users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

INSERT INTO public.users (username, password)
VALUES ('admin@acme.com', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHe1W1/JqXb9vV3s.G7/rQxY.L65A9Z4y2')
ON CONFLICT (username) DO NOTHING;

INSERT INTO acme.users (username, password)
VALUES ('admin@acme.com', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHe1W1/JqXb9vV3s.G7/rQxY.L65A9Z4y2')
ON CONFLICT (username) DO NOTHING;