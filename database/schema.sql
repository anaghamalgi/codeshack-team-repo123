

CREATE DATABASE IF NOT EXISTS smartcalc;

USE smartcalc;

CREATE TABLE IF NOT EXISTS calculation_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operation VARCHAR(80) NOT NULL,
    expression VARCHAR(500) NOT NULL,
    result VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
