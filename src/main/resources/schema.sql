-- ===========================================
-- DROP OLD TABLES (H2-compatible order)
-- ===========================================
DROP TABLE IF EXISTS POLL_TAG;
DROP TABLE IF EXISTS VOTE;
DROP TABLE IF EXISTS POLL;
DROP TABLE IF EXISTS TAG;
DROP TABLE IF EXISTS USERS;

-- ===========================================
-- USERS TABLE
-- ===========================================
CREATE TABLE USERS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    role VARCHAR(50),
    mode VARCHAR(20) NOT NULL
);

-- ===========================================
-- POLL TABLE
-- ===========================================
CREATE TABLE POLL (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question VARCHAR(255) NOT NULL,
    option_one VARCHAR(100) NOT NULL,
    option_two VARCHAR(100) NOT NULL,
    option_three VARCHAR(100),
    option_four VARCHAR(100),

    city VARCHAR(100) NOT NULL,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    option_one_votes BIGINT DEFAULT 0 NOT NULL,
    option_two_votes BIGINT DEFAULT 0 NOT NULL,
    option_three_votes BIGINT DEFAULT 0,
    option_four_votes BIGINT DEFAULT 0,

    CONSTRAINT fk_poll_user
        FOREIGN KEY (created_by)
        REFERENCES USERS(id)
);

-- ===========================================
-- TAG TABLE
-- ===========================================
CREATE TABLE TAG (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    usage_count BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uniq_tag UNIQUE (name, city)
);

-- ===========================================
-- POLL_TAG MAPPING TABLE (with CASCADE)
-- ===========================================
CREATE TABLE POLL_TAG (
    poll_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,

    PRIMARY KEY (poll_id, tag_id),

    CONSTRAINT fk_polltag_poll
        FOREIGN KEY (poll_id)
        REFERENCES POLL(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_polltag_tag
        FOREIGN KEY (tag_id)
        REFERENCES TAG(id)
        ON DELETE CASCADE
);

-- ===========================================
-- VOTE TABLE (with CASCADE on poll)
-- ===========================================
CREATE TABLE VOTE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    poll_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    selected_option INT NOT NULL,
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_vote_poll
        FOREIGN KEY (poll_id)
        REFERENCES POLL(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_vote_user
        FOREIGN KEY (user_id)
        REFERENCES USERS(id)
);
