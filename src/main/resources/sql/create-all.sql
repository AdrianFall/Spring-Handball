DROP TABLE IF EXISTS verification_token;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS account;

CREATE TABLE account
(
  id serial NOT NULL,
  email character varying(255) NOT NULL,
  password character varying(255) NOT NULL,
  enabled boolean NOT NULL DEFAULT FALSE,
  CONSTRAINT account_pkey PRIMARY KEY (id)
);

CREATE TABLE role
(
  id serial NOT NULL,
  account_id bigint NOT NULL,
  role character varying(50) NOT NULL,
  CONSTRAINT role_pkey PRIMARY KEY (id),
  CONSTRAINT role_fkey FOREIGN KEY (account_id) REFERENCES account (id)
);

INSERT INTO account(email, password, enabled)
VALUES ('jose@jo.se', '$2a$10$kMVhUDUeDSdZOeCOboXzXOwy9f4VXvIp2yE0OEHIWA.BTlaX6.vpq', TRUE);
INSERT INTO account(email, password, enabled)
VALUES ('adrianq92@hotmail.com', '$2a$10$AK1rKs1jY0W0qjACmoDioO7gzCzJIxAfXDBgOi0gfyYaf.adw8m7y', TRUE);

INSERT INTO role(account_id, role)
VALUES (1, 'ROLE_USER');
INSERT INTO role(account_id, role)
VALUES (1, 'ROLE_ADMIN');
INSERT INTO role(account_id, role)
VALUES (2, 'ROLE_USER');

CREATE TABLE verification_token
(
  id serial NOT NULL,
  token character varying(255) NOT NULL,
  expiry_date timestamp without time zone NOT NULL,
  account_id bigint NOT NULL,
  CONSTRAINT verification_token_pkey PRIMARY KEY (id),
  CONSTRAINT verification_token_fkey FOREIGN KEY (account_id) REFERENCES account(id)
);