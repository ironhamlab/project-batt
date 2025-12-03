-- PostgreSQL DDL Script
-- Drop tables if exists (역순으로 삭제)
DROP TABLE IF EXISTS transfer CASCADE;
DROP TABLE IF EXISTS seat CASCADE;
DROP TABLE IF EXISTS review_report CASCADE;
DROP TABLE IF EXISTS review CASCADE;
DROP TABLE IF EXISTS refund CASCADE;
DROP TABLE IF EXISTS performance_schedule CASCADE;
DROP TABLE IF EXISTS performance CASCADE;
DROP TABLE IF EXISTS payment CASCADE;
DROP TABLE IF EXISTS member CASCADE;
DROP TABLE IF EXISTS coin CASCADE;
DROP TABLE IF EXISTS booking_seat CASCADE;
DROP TABLE IF EXISTS booking CASCADE;
DROP TABLE IF EXISTS bid CASCADE;
DROP TABLE IF EXISTS admin_action_log CASCADE;

-- Create tables
CREATE TABLE admin_action_log
(
    id          BIGSERIAL PRIMARY KEY,
    admin_id    BIGINT NOT NULL,
    action_type VARCHAR(255) CHECK (action_type IN ('BLOCK_REVIEW', 'CREATE_PERFORMANCE', 'UPDATE_PERFORMANCE')),
    description VARCHAR(255),
    target_type VARCHAR(255) CHECK (target_type IN ('REVIEW', 'PERFORMANCE', 'USER')),
    created_at  TIMESTAMPTZ,
    updated_at  TIMESTAMPTZ
);

CREATE TABLE bid
(
    id          BIGSERIAL PRIMARY KEY,
    bidder_id   BIGINT NOT NULL,
    transfer_id BIGINT NOT NULL,
    bid_amount  INTEGER,
    created_at  TIMESTAMPTZ,
    updated_at  TIMESTAMPTZ
);

CREATE TABLE booking
(
    id                      BIGSERIAL PRIMARY KEY,
    member_id               BIGINT       NOT NULL,
    performance_schedule_id BIGINT       NOT NULL,
    order_id                VARCHAR(255) NOT NULL UNIQUE,
    payment_key             VARCHAR(255),
    payment_method          VARCHAR(255) CHECK (payment_method IN ('TOSS')),
    status                  VARCHAR(255) CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'TRANSFERRED')),
    seat_count              INTEGER,
    total_amount            INTEGER,
    cancellation_fee        INTEGER,
    cancelled_date          TIMESTAMPTZ,
    created_at              TIMESTAMPTZ,
    updated_at              TIMESTAMPTZ
);

CREATE TABLE booking_seat
(
    id             BIGSERIAL PRIMARY KEY,
    booking_id     BIGINT       NOT NULL,
    seat_id        BIGINT       NOT NULL,
    booking_number VARCHAR(255) NOT NULL UNIQUE,
    price          INTEGER,
    created_at     TIMESTAMPTZ,
    updated_at     TIMESTAMPTZ
);

CREATE TABLE coin
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL,
    transaction_type VARCHAR(255) CHECK (transaction_type IN
                                         ('SIGN_UP', 'REVIEW', 'TRANSFER_SUCCESS', 'TRANSFER_FAIL', 'BID_SUCCESS', 'BID_FAIL')),
    amount           INTEGER,
    remain_coin      INTEGER,
    description      VARCHAR(255),
    created_at       TIMESTAMPTZ,
    updated_at       TIMESTAMPTZ
);

CREATE TABLE member
(
    id             BIGSERIAL PRIMARY KEY,
    email          VARCHAR(255) NOT NULL UNIQUE,
    name           VARCHAR(255) NOT NULL,
    phone_number   VARCHAR(255),
    birth          VARCHAR(255),
    sex            VARCHAR(255) CHECK (sex IN ('MAN', 'WOMAN')),
    provider_type  VARCHAR(255) CHECK (provider_type IN ('KAKAO', 'NAVER')),
    provider_id    VARCHAR(255),
    role           VARCHAR(255) CHECK (role IN ('USER', 'ADMIN')),
    coin_balance   INTEGER,
    available_coin INTEGER,
    is_active      BOOLEAN,
    created_at     TIMESTAMPTZ,
    updated_at     TIMESTAMPTZ,
    deleted_at     TIMESTAMPTZ
);

CREATE TABLE payment
(
    id          BIGSERIAL PRIMARY KEY,
    booking_id  BIGINT       NOT NULL UNIQUE,
    payment_key VARCHAR(255) NOT NULL UNIQUE,
    method      VARCHAR(255) CHECK (method IN ('TOSS')),
    status      VARCHAR(255) CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED', 'FAILED')),
    amount      INTEGER,
    created_at  TIMESTAMPTZ,
    updated_at  TIMESTAMPTZ
);

CREATE TABLE performance
(
    id                     BIGSERIAL PRIMARY KEY,
    title                  VARCHAR(255) NOT NULL,
    description            TEXT,
    genre                  VARCHAR(255) CHECK (genre IN ('MUSICAL', 'CONCERT', 'THEATER', 'DANCE', 'CLASSIC')),
    venue_name             VARCHAR(255),
    venue_address          VARCHAR(255),
    poster_image_url       VARCHAR(255),
    description_url        VARCHAR(255),
    duration_minute        INTEGER,
    age_restriction        INTEGER,
    status                 VARCHAR(255) CHECK (status IN ('SCHEDULE', 'OPEN', 'END')),
    performance_start_date TIMESTAMP,
    performance_end_date   TIMESTAMP,
    booking_open_date      TIMESTAMP,
    created_at             TIMESTAMPTZ,
    updated_at             TIMESTAMPTZ
);

CREATE TABLE performance_schedule
(
    id               BIGSERIAL PRIMARY KEY,
    performance_id   BIGINT NOT NULL,
    performance_date DATE,
    performance_time TIME,
    status           VARCHAR(255) CHECK (status IN ('SCHEDULED', 'CANCELLED', 'COMPLETED')),
    total_seat       INTEGER,
    available_seat   INTEGER,
    created_at       TIMESTAMPTZ,
    updated_at       TIMESTAMPTZ
);

CREATE TABLE refund
(
    id               BIGSERIAL PRIMARY KEY,
    payment_id       BIGINT NOT NULL UNIQUE,
    status           VARCHAR(255) CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    refund_amount    INTEGER,
    cancellation_fee INTEGER,
    reason           VARCHAR(255),
    created_at       TIMESTAMPTZ,
    updated_at       TIMESTAMPTZ
);

CREATE TABLE review
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT           NOT NULL,
    performance_id BIGINT           NOT NULL,
    booking_id     BIGINT           NOT NULL,
    content        TEXT,
    rating         DOUBLE PRECISION NOT NULL,
    created_at     TIMESTAMPTZ,
    updated_at     TIMESTAMPTZ
);

CREATE TABLE review_report
(
    id             BIGSERIAL PRIMARY KEY,
    reporter_id    BIGINT NOT NULL,
    review_id      BIGINT NOT NULL,
    report_reason  VARCHAR(255) CHECK (report_reason IN
                                       ('OFF_TOPIC', 'ILLEGAL_CONTENT', 'FALSE_INFORMATION', 'SPAM_OR_ADVERTISEMENT',
                                        'INAPPROPRIATE_LANGUAGE', 'HARASSMENT_OR_DEFAMATION', 'PERSONAL_INFORMATION_EXPOSURE',
                                        'OTHER')),
    report_content VARCHAR(255),
    status         VARCHAR(255) CHECK (status IN ('PENDING', 'PROCESSED', 'REJECTED')),
    created_at     TIMESTAMPTZ,
    updated_at     TIMESTAMPTZ
);

CREATE TABLE seat
(
    id                      BIGSERIAL PRIMARY KEY,
    performance_schedule_id BIGINT  NOT NULL,
    number                  INTEGER NOT NULL,
    grade                   VARCHAR(255) CHECK (grade IN ('VIP', 'R', 'S', 'A', 'B')),
    zone                    VARCHAR(255),
    floor                   INTEGER,
    row                     INTEGER,
    x                       VARCHAR(255),
    y                       VARCHAR(255),
    price                   INTEGER,
    status                  VARCHAR(255) CHECK (status IN ('AVAILABLE', 'RESERVED')),
    created_at              TIMESTAMPTZ,
    updated_at              TIMESTAMPTZ
);

CREATE TABLE transfer
(
    id                     BIGSERIAL PRIMARY KEY,
    seller_id              BIGINT NOT NULL,
    booking_id             BIGINT NOT NULL,
    status                 VARCHAR(255) CHECK (status IN ('ACTIVE', 'ENDED')),
    current_highest_bid    INTEGER,
    transfer_end_date_time TIMESTAMPTZ,
    created_at             TIMESTAMPTZ,
    updated_at             TIMESTAMPTZ
);

-- Add foreign key constraints
ALTER TABLE bid
    ADD CONSTRAINT fk_bid_bidder FOREIGN KEY (bidder_id) REFERENCES member (id);
ALTER TABLE bid
    ADD CONSTRAINT fk_bid_transfer FOREIGN KEY (transfer_id) REFERENCES transfer (id);

ALTER TABLE booking
    ADD CONSTRAINT fk_booking_member FOREIGN KEY (member_id) REFERENCES member (id);
ALTER TABLE booking
    ADD CONSTRAINT fk_booking_schedule FOREIGN KEY (performance_schedule_id) REFERENCES performance_schedule (id);

ALTER TABLE booking_seat
    ADD CONSTRAINT fk_booking_seat_booking FOREIGN KEY (booking_id) REFERENCES booking (id);
ALTER TABLE booking_seat
    ADD CONSTRAINT fk_booking_seat_seat FOREIGN KEY (seat_id) REFERENCES seat (id);

ALTER TABLE coin
    ADD CONSTRAINT fk_coin_user FOREIGN KEY (user_id) REFERENCES member (id);

ALTER TABLE payment
    ADD CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES booking (id);

ALTER TABLE performance_schedule
    ADD CONSTRAINT fk_schedule_performance FOREIGN KEY (performance_id) REFERENCES performance (id);

ALTER TABLE refund
    ADD CONSTRAINT fk_refund_payment FOREIGN KEY (payment_id) REFERENCES payment (id);

ALTER TABLE review
    ADD CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES member (id);
ALTER TABLE review
    ADD CONSTRAINT fk_review_performance FOREIGN KEY (performance_id) REFERENCES performance (id);
ALTER TABLE review
    ADD CONSTRAINT fk_review_booking FOREIGN KEY (booking_id) REFERENCES booking (id);

ALTER TABLE review_report
    ADD CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES member (id);
ALTER TABLE review_report
    ADD CONSTRAINT fk_report_review FOREIGN KEY (review_id) REFERENCES review (id);

ALTER TABLE seat
    ADD CONSTRAINT fk_seat_schedule FOREIGN KEY (performance_schedule_id) REFERENCES performance_schedule (id);

ALTER TABLE transfer
    ADD CONSTRAINT fk_transfer_seller FOREIGN KEY (seller_id) REFERENCES member (id);
ALTER TABLE transfer
    ADD CONSTRAINT fk_transfer_booking FOREIGN KEY (booking_id) REFERENCES booking (id);