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

INSERT INTO TB_FORM VALUES (100,'test','Classic Car Application',1,'thos os a test',null,(SELECT TOP 1 current_timestamp FROM INFORMATION_SCHEMA.SYSTEM_TABLES));
INSERT INTO TB_FORM VALUES (101,'test2','Invoice',1,'thos os a test',null,(SELECT TOP 1 current_timestamp FROM INFORMATION_SCHEMA.SYSTEM_TABLES));
INSERT INTO TB_FORM VALUES (102,'test3','Customer Satisfaction Survey',1,'thos os a test new',null,(SELECT TOP 1 current_timestamp FROM INFORMATION_SCHEMA.SYSTEM_TABLES));