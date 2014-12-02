CREATE TABLE users (
    token varchar(255) primary key
    ,name varchar(255) not null
    ,login varchar(255) not null
    ,gender integer not null
    ,birthday varchar(255) not null
    ,picture_url varchar(255)
    ,facebook_token varchar(255)
    ,facebook_id varchar(255)
    ,mail varchar(255) not null
);

CREATE TABLE wishlists (
     _id int primary key
    ,title varchar(255) not null
    ,description varchar(255) not null
    ,last_update varchar(255) not null
);

CREATE TABLE wishlist_products (
    _id integer primary key
    ,name varchar(255) not null
    ,nickname varchar(255)
    ,picture_url varchar(255)
    ,last_update varchar(255) not null
    ,id_wishlist integer not null
);

CREATE TABLE product_manufacturers (
    _id integer primary key
    ,name varchar(255) not null
    ,last_update varchar(255) not null
    ,id_product integer not null
);