CREATE TABLE products (
    ID int primary key NOT NULL AUTO_INCREMENT,
    name varchar(255),
    type varchar(255)
);

CREATE TABLE contracts (
    ID int primary key NOT NULL AUTO_INCREMENT,
    product int,
    revenue decimal,
    dateSigned date
);

CREATE TABLE revenueRecognitions (
    contract int,
    amount decimal,
    recognizedOn date,
    PRIMARY KEY (contract, recognizedOn)
);
