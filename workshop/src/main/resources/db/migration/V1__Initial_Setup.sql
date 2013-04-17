-- SQ_FORM

CREATE SEQUENCE SQ_FORM AS INTEGER
START WITH 1
INCREMENT BY 1;

-- TB_FORM

CREATE TABLE TB_FORM
(
  ID            INTEGER               NOT NULL PRIMARY KEY,
  NAME          VARCHAR(50)           NOT NULL,
  LABEL         VARCHAR(100)          NOT NULL,
  VERSION       INTEGER               NOT NULL,
  DEFINITION    CLOB                  NOT NULL,
  COMPILED      CLOB                  NULL,
  CREATED		TIMESTAMP			  NOT NULL,
);

-- SQ_FORM_DATA

CREATE SEQUENCE SQ_FORM_DATA AS INTEGER
START WITH 1
INCREMENT BY 1;

-- TB_FORM_DATA

CREATE TABLE TB_FORM_DATA
(
  ID               INTEGER               NOT NULL PRIMARY KEY,
  FORM_ID          INTEGER               NOT NULL,
  DATA             CLOB                  NOT NULL,
  CREATED		   TIMESTAMP			 NOT NULL,
);

CREATE TABLE TB_FORM_KEYS
(
  ID               INTEGER               NOT NULL PRIMARY KEY,
  KEY              VARCHAR(100)          NOT NULL,
  VALUE            VARCHAR(1000)         NOT NULL,
);

INSERT INTO TB_FORM VALUES (100,'test','test',1,'thos os a test',null,(SELECT TOP 1 current_timestamp FROM INFORMATION_SCHEMA.SYSTEM_TABLES));
INSERT INTO TB_FORM VALUES (101,'test2','test2',1,'thos os a test',null,(SELECT TOP 1 current_timestamp FROM INFORMATION_SCHEMA.SYSTEM_TABLES));
INSERT INTO TB_FORM VALUES (102,'test','test',2,'thos os a test new',null,(SELECT TOP 1 current_timestamp FROM INFORMATION_SCHEMA.SYSTEM_TABLES));