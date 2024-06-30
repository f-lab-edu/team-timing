CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE products (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   price int NOT NULL,
   status VARCHAR(255) NOT NULL
);

create table productDetails
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    color            varchar(255) null,
    productCondition      varchar(255) null,
    batteryCondition varchar(255) null,
    cameraCondition  varchar(255) null,
    accessories      varchar(255) null,
    purchaseDate     varchar(255) null,
    warrantyDuration varchar(255) null,
    tradeLocation    varchar(255) null,
    deliveryFee      int          null
)
