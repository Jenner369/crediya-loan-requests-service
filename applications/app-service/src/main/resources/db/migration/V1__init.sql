CREATE TABLE statuses (
                          code VARCHAR(50) PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT
);


CREATE TABLE loan_types (
                            code VARCHAR(50) PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            min_amount NUMERIC(15,2) NOT NULL,
                            max_amount NUMERIC(15,2) NOT NULL,
                            interest_rate NUMERIC(5,2) NOT NULL,
                            auto_approval BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE loan_applications (
                                   id CHAR(36) PRIMARY KEY,
                                   amount NUMERIC(15,2) NOT NULL,
                                   term INT NOT NULL,
                                   identity_document VARCHAR(50) NOT NULL,
                                   email VARCHAR(255) NOT NULL,
                                   status_code VARCHAR(50) NOT NULL REFERENCES statuses(code),
                                   loan_type_code VARCHAR(50) NOT NULL REFERENCES loan_types(code)
);