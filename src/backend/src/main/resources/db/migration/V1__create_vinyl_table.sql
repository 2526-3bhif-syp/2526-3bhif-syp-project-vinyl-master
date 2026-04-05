CREATE TABLE IF NOT EXISTS vinyl (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    artist TEXT NOT NULL,
    genre TEXT,
    year INTEGER NOT NULL,
    price NUMERIC(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vinyl_title_artist_year ON vinyl(title, artist, year);

CREATE TABLE IF NOT EXISTS genre (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO genre (name) VALUES 
    ('Rock'), 
    ('Pop'), 
    ('Jazz'), 
    ('Classical'), 
    ('Hip Hop'), 
    ('Electronic'), 
    ('Country'), 
    ('Blues')
ON CONFLICT (name) DO NOTHING;
