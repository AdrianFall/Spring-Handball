DROP TABLE IF EXISTS Test;
DROP TABLE IF EXISTS UserConnection;
DROP TABLE IF EXISTS persistent_logins;
DROP TABLE IF EXISTS password_reset_token;
DROP TABLE IF EXISTS verification_token;
DROP TABLE IF EXISTS accounts_roles;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS account;


CREATE TABLE account
(
  id serial NOT NULL,
  email character varying(255) NOT NULL,
  password character varying(255) NOT NULL,
  sign_in_provider character varying(20),
  enabled boolean NOT NULL DEFAULT FALSE,
  CONSTRAINT account_pkey PRIMARY KEY (id)
);

CREATE TABLE role
(
  id serial NOT NULL,
  role character varying(50) NOT NULL,
  CONSTRAINT role_pkey PRIMARY KEY (id)
);

INSERT INTO account(email, password, enabled) VALUES ('jose@jo.se', '$2a$10$kMVhUDUeDSdZOeCOboXzXOwy9f4VXvIp2yE0OEHIWA.BTlaX6.vpq', TRUE);
INSERT INTO account(email, password, enabled) VALUES ('adrianq92@hotmail.com', '$2a$10$AK1rKs1jY0W0qjACmoDioO7gzCzJIxAfXDBgOi0gfyYaf.adw8m7y', TRUE);

INSERT INTO role(role) VALUES ('ROLE_USER');
INSERT INTO role(role) VALUES ('ROLE_ADMIN');

CREATE TABLE accounts_roles
(
	account_id bigint NOT NULL,
	role_id bigint NOT NULL,
	CONSTRAINT usersroles_accid_fkey FOREIGN KEY (account_id) REFERENCES account (id),
	CONSTRAINT usersroles_roleid_fkey FOREIGN KEY (role_id) REFERENCES role (id)
);

INSERT INTO accounts_roles(account_id, role_id) VALUES (1, 1);
INSERT INTO accounts_roles(account_id, role_id) VALUES (1, 2);
INSERT INTO accounts_roles(account_id, role_id) VALUES (2, 1);
INSERT INTO accounts_roles(account_id, role_id) VALUES (2, 2);

CREATE TABLE verification_token
(
  id serial NOT NULL,
  token character varying(255) NOT NULL,
  expiry_date timestamp without time zone NOT NULL,
  account_id bigint NOT NULL,
  CONSTRAINT verification_token_pkey PRIMARY KEY (id),
  CONSTRAINT verification_token_fkey FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE TABLE password_reset_token
(
  id serial NOT NULL,
  token character varying(255) NOT NULL,
  account_id bigint NOT NULL,
  expiry_date timestamp without time zone NOT NULL,
  CONSTRAINT password_reset_token_pkey PRIMARY KEY (id),
  CONSTRAINT password_reset_token_fkey FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE TABLE persistent_logins
(
  username VARCHAR(64) NOT NULL,
  series VARCHAR(64) NOT NULL,
  token VARCHAR(64) NOT NULL,
  last_used TIMESTAMP NOT NULL,
  PRIMARY KEY (series)
);

create table UserConnection (userId varchar(255) not null,
    providerId varchar(255) not null,
    providerUserId varchar(255),
    rank int not null,
    displayName varchar(255),
    profileUrl varchar(512),
    imageUrl varchar(512),
    accessToken varchar(255) not null,
    secret varchar(255),
    refreshToken varchar(255),
    expireTime bigint,
    primary key (userId, providerId, providerUserId));
create unique index UserConnectionRank on UserConnection(userId, providerId, rank);

create table Test (
  id serial not null,
  account_id bigint NOT NULL,
  test_word varchar(100),
  CONSTRAINT test_pkey PRIMARY KEY (id),
  CONSTRAINT test_fkey FOREIGN KEY (account_id) REFERENCES account(id)
);