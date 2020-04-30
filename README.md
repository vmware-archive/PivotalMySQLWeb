# PivotalMySQLWeb

PivotalMySQLWeb is a free Pivotal open source project, intended to handle the administration of a Pivotal MySQL Service
Instance over the Web. PivotalMySQLWeb supports a wide range of operations on a Pivotal MySQL Service Instance such as
managing tables, views, indexes which can all be performed via the user interface, while you still have the ability to
directly execute any number of SQL statements.

It includes the following capabilities:

- Multiple Command SQL worksheet for DDL and DML
- Run Explain Plan across SQL Statements
- View/Run DDL command against Tables/Views/Indexes/Constraints
- Command History
- Auto Bind to Pivotal MySQL Services bound to the Application within Pivotal Cloud Foundry (PCF)
- Manage JDBC Connections
- Load SQL File into SQL Worksheet from Local File System
- SQL Worksheet with syntax highlighting support
- HTTP GET request to auto login without a login form
- Export SQL query results in JSON or CSV formats

![alt tag](https://image.ibb.co/kxYJLk/piv_mysqlweb1.png)

## Run stand alone outside of PCF

```sh
git clone https://github.com/pivotal-cf/PivotalMySQLWeb.git
cd PivotalMySQLWeb
./mvnw -DskipTests=true package
./mvnw spring-boot:run
```

Note: If you opt-in to running tests, you must have Docker installed. We employ the [MySQL container](https://mvnrepository.com/artifact/org.testcontainers/mysql) from [testcontainers.org](https://www.testcontainers.org) library.

```txt
./mvnw spring-boot:run

...

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.1.0.RELEASE)

...

2016-07-03 17:13:44.164  INFO 19664 --- [           main] .m.m.a.ExceptionHandlerExceptionResolver : Detected @ExceptionHandler methods in repositoryRestExceptionHandler
2016-07-03 17:13:44.225  INFO 19664 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Registering beans for JMX exposure on startup
2016-07-03 17:13:44.291  INFO 19664 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8080 (http)
2016-07-03 17:13:44.294  INFO 19664 --- [           main] c.p.p.m.PivotalMySqlWebApplication       : Started PivotalMySqlWebApplication in 3.4 seconds (JVM running for 3.761)
```

Access and connect to your MySQL instance using the default username/password as follows:

Default username = admin
Default password = cfmysqlweb

```txt
http://localhost:8080/
```

Note: When connecting to a MySQL database instance ensure you JDBC URL includes a database name as shown below which e.g., targets the "employees" database:

```txt
jdbc:mysql://localhost:3306/employees
```

![alt tag](https://image.ibb.co/f3SzLk/piv_mysqlweb2.png)

## Deploy to Pivotal Cloud Foundry

To deploy to Pivotal Cloud Foundry it's best to bind the application to a Pivotal MySQL service instance so it automatically connects
to the MySQL instance as shown in the sample manifest below. If you don't bind to a MySQL instance it will simply ask you to login
to a MySQL instance itself.

```yml
applications:
- name: pivotal-mysqlweb
  memory: 1024M
  instances: 1
  random-route: true
  path: ./target/PivotalMySQLWeb-1.0.0-SNAPSHOT.jar
  services:
    - some-mysql-database-instance
  env:
    JAVA_OPTS: -Djava.security.egd=file:///dev/urandom
```

Push to PCF using:

```sh
cf push -f manifest.yml
```

## Screen Shots

![alt tag](https://image.ibb.co/kKG6rF/piv_mysqlweb3.png)

![alt tag](https://image.ibb.co/f9rZdv/piv_mysqlweb4.png)

![alt tag](https://image.ibb.co/bWG0Jv/piv_mysqlweb5.png)

![alt tag](https://image.ibb.co/bBCJ5a/piv_mysqlweb6.png)

## SQL Worksheet - Max Records to Display

You can control the number of records to display in the "SQL Worksheet" using the "Preferences" page. To do that follow these steps:

1. On the top menu bar select "Menu -> Preferences"
2. Set the value for "Max Records in Worksheet" to the value you require it should be more then 30 by default unless it was changed prior to deployment
3. Click "Update Preferences"

Alternatively you can also set that at deployment to use a default value by editing `main/resources/preferences.properties` and setting the property below:

```txt
maxRecordsinSQLQueryWindow=500
```

## Security - HTTP Basic Authentication

By default this application is using HTTP Basic Authentication to protect every end point. The username/password is set in
`main/resources/application-cloud.yml` file and can be altered here prior to repacking/deploying.

Default username = **admin**
Default password = **cfmysqlweb**

```yml
spring:
  security:
    user:
      name: admin
      password: cfmysqlweb
```

Note: When running outside of Cloud Foundry Security is disabled as per this code

```java

	@Profile("!cloud")
	@Configuration
	static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

		@Override
		public void configure(WebSecurity web) throws Exception {
			web
					.ignoring()
					.antMatchers("/**");
		}
	}
	
```

## Stored Procedures/Functions

PivotalMySQLWeb does not support the use of the DELIMITER statement. DELIMITER statement is for clients which do not have a way to 
have a terminated string, such as MySQL command line client. As a result the use of DELIMITER statement is not needed in JDBC and
hence not supported in this tool. So to create a stored procedure or function you can do so using a single stored procedure or function.
This tool does not support stored procedures or functions as multiple SQL statements so if you wish to create stored procedures you would
create one at a time in the SQLWorksheet and some examples of what they look like are as follows

_Note: DELIMITER is not required and/or supported_

```mysql

CREATE PROCEDURE spEmployee(empno decimal)
BEGIN
    SELECT
        Emp.empno,
        Emp.ename
    FROM
        Emp emp
    WHERE
        Emp.empno = empno;
END;

CREATE PROCEDURE spEmployee1(empno decimal)
BEGIN
END;

CREATE FUNCTION hello (s CHAR(20)) RETURNS CHAR(50) DETERMINISTIC
BEGIN
  RETURN CONCAT('Hello, ',s,'!');
END;
```

## Automatic Connection to Bound MySQL instance

Note: If you haver multiple bound MySQL service instances then....

PivotalMySQLWeb will search through all the VCAP_SERVICES and connect to the first MySQL Database Service instance it finds in this order

```
  clearDB
  p-mysql (v1 instances)
  p.mysql (v2 instances)
  GCP Cloud SQL service broker instance
  mariadbent
  aws_aurora
  mariadb - if using minibroker here https://github.com/kubernetes-sigs/minibroker
```

If you have bound your application to multiple MySQL service instances then you can switch to the other Instances using the menu option 
"Instances" on the navigation bar as shown below

![alt tag](https://i.ibb.co/j6gTRM3/piv-mysqlweb7.png)

  
![alt tag](https://image.ibb.co/iCvjc5/Pivotal-My-SQLWeb-BLOG.png)

Pas Apicella [papicella at pivotal.io] is an Advisory Platform Architect at Pivotal Australia

