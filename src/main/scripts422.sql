CREATE TABLE car (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price > 0)
);

CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INTEGER NOT NULL CHECK (age >= 0),
    has_license BOOLEAN NOT NULL DEFAULT FALSE,
    car_id INTEGER,
    CONSTRAINT fk_person_car
        FOREIGN KEY (car_id)
        REFERENCES car(id)
        ON DELETE SET NULL
);

CREATE INDEX idx_person_car_id ON person(car_id);
