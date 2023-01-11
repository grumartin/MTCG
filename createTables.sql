DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users
(
    u_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    coins INTEGER NOT NULL DEFAULT 20,
    bio VARCHAR(300) NULL,
    token VARCHAR(50) NULL
);
INSERT INTO users VALUES(-1, 'draw', 'draw', 0, '', '');

DROP TABLE IF EXISTS user_stats CASCADE;
CREATE TABLE user_stats(
    s_id SERIAL PRIMARY KEY,
    elo INTEGER DEFAULT 100,
    wins INTEGER DEFAULT 0,
    losses INTEGER DEFAULT 0,
    total INTEGER DEFAULT 0,
    user_id INTEGER,
    CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users (u_id)
);

DROP TABLE IF EXISTS package CASCADE;
CREATE TABLE package(
    p_id SERIAL PRIMARY KEY,
    price INTEGER NOT NULL DEFAULT 5,
    p_name VARCHAR(100) NOT NULL DEFAULT 'CARDS'
);

DROP TABLE IF EXISTS battle CASCADE;
CREATE TABLE battle(
    b_id SERIAL NOT NULL PRIMARY KEY,
    playerA INTEGER NULL,
    playerB INTEGER NULL,
    winner INTEGER NULL,
    CONSTRAINT fk_playerA FOREIGN KEY (playerA) REFERENCES users (u_id),
    CONSTRAINT fk_playerB FOREIGN KEY (playerB) REFERENCES users (u_id),
    CONSTRAINT fk_winner FOREIGN KEY (winner) REFERENCES users (u_id)
);

DROP TABLE IF EXISTS battle_round CASCADE;
CREATE TABLE battle_round(
    r_id SERIAL NOT NULL PRIMARY KEY,
    cardA VARCHAR(100) NOT NULL,
    cardB VARCHAR(100) NOT NULL,
    winner VARCHAR(100),
    b_id INTEGER NOT NULL,
    CONSTRAINT fk_cardA FOREIGN KEY (cardA) REFERENCES cards (c_id),
    CONSTRAINT fk_cardB FOREIGN KEY (cardB) REFERENCES cards (c_id),
    CONSTRAINT fk_winner FOREIGN KEY (winner) REFERENCES cards (c_id),
    CONSTRAINT fk_battle_id FOREIGN KEY (b_id) REFERENCES battle (b_id)
);


DROP TABLE IF EXISTS deck CASCADE;
CREATE TABLE deck(
    d_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users (u_id)
);

DROP TYPE IF EXISTS CardType CASCADE;
CREATE TYPE CardType AS ENUM ('monster', 'spell');
DROP TABLE IF EXISTS cards CASCADE;
CREATE TABLE cards(
    c_id VARCHAR(100) NOT NULL PRIMARY KEY,
    c_name VARCHAR(100) NOT NULL,
    c_dmg DECIMAL NULL,
    pckg_id INTEGER NULL,
    user_id INTEGER NULL,
    deck_id INTEGER NULL,
    type CardType NOT NULL,
    CONSTRAINT fk_package FOREIGN KEY (pckg_id) REFERENCES package (p_id),
    CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users (u_id),
    CONSTRAINT fk_deck FOREIGN KEY (deck_id) REFERENCES deck (d_id)
);

DROP TYPE IF EXISTS TradingType CASCADE;
CREATE TYPE TradingType AS ENUM ('monster', 'spell');
DROP TABLE IF EXISTS trading_deal;
CREATE TABLE trading_deal(
    t_id VARCHAR(100) NOT NULL PRIMARY KEY,
    first_card VARCHAR(100) NOT NULL,
    seller INTEGER,
    CONSTRAINT fk_card_one FOREIGN KEY (first_card) REFERENCES cards (c_id),
    CONSTRAINT fk_seller FOREIGN KEY (seller) REFERENCES users (u_id),
    type TradingType NULL,
    min_dmg INTEGER NULL
);