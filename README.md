# PTT Forestation Project
---------------------------

## Setting up


### Prepare the Database

**Enter the table details via the wizard**

```
CREATE TABLE USERS (
  `USERID` VARCHAR(255) NOT NULL,
  `PASSWORD` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`USERID`)
);

CREATE TABLE USERS_GROUPS (
  `GROUPID` VARCHAR(20) NOT NULL,
  `USERID` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`GROUPID`)
);
```

### Create a connector pool in Admin Console as follows:

1. Name				: `ForsetPool`
2. Resource Type	: `javax.sql.DataSource`
3. Database Vendor	: `MySql`


### Create New Database Connection:

1. URL		: `jdbc:mysql://[YOUR_HOST]:3306/forest`
2. User		: `root`
3. Password	: `[YOUR_PASSWORD]`

### then, add the following properties:
1. serverName	: `[YOUR_HOST]`
2. port			: `3306`
3. databaseName	: `forest`
4. user			: `root`
5. password		: `[YOUR_PASSWORD]`

### Create a DataSource `jdbc/forest` associated with the above pool.

### Create a JDBCRealm, named jdbcrealm with the following properties:

1. realm-name			:	`forestRealm`
2. jaas-context			:	`jdbcRealm`
3. datasource-jndi		:	`jdbc/forest`
4. user-table			:	`USERS`
5. user-name-column		:	`USERID`
6. password-name-column	:	`PASSWORD`
7. group-table			:	`USERS_GROUPS`
8. group-name-column	:	`GROUPID`
9. digest-algorithm		:	`MD5`