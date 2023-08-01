DROP TABLE IF EXISTS endpoint_hit CASCADE;

CREATE TABLE IF NOT EXISTS endpoint_hit (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app VARCHAR(255) NOT NULL,
    uri VARCHAR(120) NOT NULL,
    ip VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_endpoint_hit PRIMARY KEY (id)
);