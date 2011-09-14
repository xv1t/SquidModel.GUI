/**/ 
create table ONCE_PARAM(
	ID		integer,
	PARAM_NAME	varchar(55),
	STRING_VALUE	varchar(1024),
	INT_VALUE	integer,
	BOOL_VALUE	char(1),
	DISABLED	char(1),
	INDEX_POS	integer
);
/**/
create generator SEQ_ONCE_PARAM_ID;