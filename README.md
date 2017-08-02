<img src="https://image.ibb.co/iCvjc5/Pivotal_My_SQLWeb_BLOG.png" height="120" width="350"/>

PivotalMySQLWeb is a free Pivotal open source project, intended to handle the administration of a Pivotal MySQL Service 
Instance over the Web. PivotalMySQLWeb supports a wide range of operations on a Pivotal MySQL Service Instance such as 
managing tables, views, indexes which can all be performed via the user interface, while you still have the ability to 
directly execute any number of SQL statements

It includes the following capabilities:

<ul>
    <li>Multiple Command SQL worksheet for DDL and DML</li>
    <li>Run Explain Plan across SQL Statements</li>
    <li>View/Run DDL command against Tables/Views/Indexes/Constraints</li>
    <li>Command History</li>
    <li>Auto Bind to Pivotal MySQL Services bound to the Application within Pivotal Cloud Foundry (PCF)</li>
    <li>Manage JDBC Connections</li>
    <li>Load SQL File into SQL Worksheet from Local File System</li>
    <li>SQL Worksheet with syntax highlighting support</li>
    <li>HTTP GET request to auto login without a login form</li>
    <li>Export SQL query results in JSON or CSV formats</li>
</ul>

![alt tag](https://image.ibb.co/kxYJLk/piv_mysqlweb1.png)

<h3>Run stand alone outside of PCF</h3>

- $ git clone https://github.com/pivotal-cf/PivotalMySQLWeb.git
- $ cd PivotalMySQLWeb
- $ mvn -DskipTests=true package
- Run as follows

```
papicella@papicella:~/pivotal/DemoProjects/spring-starter/pivotal/PivotalMySQLWeb$ java -jar ./target/PivotalMySQLWeb-0.0.1-SNAPSHOT.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v1.3.5.RELEASE)

...

2016-07-03 17:13:44.164  INFO 19664 --- [           main] .m.m.a.ExceptionHandlerExceptionResolver : Detected @ExceptionHandler methods in repositoryRestExceptionHandler
2016-07-03 17:13:44.225  INFO 19664 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Registering beans for JMX exposure on startup
2016-07-03 17:13:44.291  INFO 19664 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8080 (http)
2016-07-03 17:13:44.294  INFO 19664 --- [           main] c.p.p.m.PivotalMySqlWebApplication       : Started PivotalMySqlWebApplication in 3.4 seconds (JVM running for 3.761)
```

- Access as follows and connect to your MySQL instance

```
http://localhost:8080/
```

![alt tag](https://image.ibb.co/f3SzLk/piv_mysqlweb2.png)

<h3>Deploy to Pivotal Cloud Foundry</h3>

To deploy to Pivotal Cloud Foundry it's best to bind the application to a Pivotal MySQL service instance so it automatically connects
to the MySQL instance as shown in the sample manifest below. If you don't bind to a MySQL instance it will simply ask you to login 
to a MySQL instance itself.

```
---
applications:
- name: pivotal-mysqlweb
  memory: 1024M
  instances: 1
  random-route: true
  path: ./target/PivotalMySQLWeb-0.0.1-SNAPSHOT.jar
  services:
    - pas-mysql-dedicated-v2
  env:
    JAVA_OPTS: -Djava.security.egd=file:///dev/urando
```

Push to PCF using

```
$ cf push -f manifest.yml
```

<h3>Screen Shots</h3>

![alt tag](https://image.ibb.co/kKG6rF/piv_mysqlweb3.png)

![alt tag](https://image.ibb.co/f9rZdv/piv_mysqlweb4.png)

![alt tag](https://image.ibb.co/bWG0Jv/piv_mysqlweb5.png)

![alt tag](https://image.ibb.co/bBCJ5a/piv_mysqlweb6.png)


<hr />
Pas Apicella [papicella at pivotal.io] is a Senior Platform Architect at Pivotal Australia 
