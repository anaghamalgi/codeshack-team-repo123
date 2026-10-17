
-- Create the SmartCalc database
CREATE DATABASE IF NOT EXISTS smartcalc;

USE smartcalc;

-- Store calculation history
CREATE TABLE IF NOT EXISTS calculation_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operation VARCHAR(50) NOT NULL,
    input_data TEXT NOT NULL,
    result VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
