-- SQ_FORM

CREATE SEQUENCE SQ_FORM AS INTEGER
START WITH 1
INCREMENT BY 1;

-- TB_FORM

CREATE TABLE TB_FORM
(
  ID            INTEGER               NOT NULL PRIMARY KEY,
  NAME          VARCHAR(50)           NOT NULL,
  VERSION       INTEGER               NOT NULL,
  DEFINITION    CLOB                  NOT NULL,
);

INSERT INTO TB_FORM VALUES (1,'test',1,'thos os a test');
INSERT INTO TB_FORM VALUES (2,'test2',1,'thos os a test');
INSERT INTO TB_FORM VALUES (3,'test',2,'thos os a test new');