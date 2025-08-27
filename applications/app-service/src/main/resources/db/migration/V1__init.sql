CREATE TABLE statuses (
                          id CHAR(36) PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT
);


CREATE TABLE loan_types (
                            id CHAR(36) PRIMARY KEY,
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
                                   status_id CHAR(36) NOT NULL REFERENCES statuses(id),
                                   loan_type_id CHAR(36) NOT NULL REFERENCES loan_types(id)
);