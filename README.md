<h1>Pivotal MySQL*Web </h1>

PivotalMySQL*Web is a browser based SQL tool rendered using Bootstrap UI for MySQL PCF service instances which allows you to 
run SQL commands and view schema objects from a browser based interface. It includes the following capabilities

<ul>
    <li>Multiple Command SQL worksheet for DDL and DML</li>
    <li>Run Explain Plan across SQL Statements</li>
    <li>View/Run DDL command against Tables/Views [More Schema Objects support Coming]</li>
    <li>Command History</li>
    <li>Auto Bind to ClearDB and Pivotal MySQL Services bound to the Application within Pivotal Cloud Foundry (PCF)</li>
    <li>Manage JDBC Connections</li>
    <li>Load SQL File into SQL Worksheet from Local File Syste</li>
</ul>

![alt tag](https://dl.dropboxusercontent.com/u/15829935/platform-demos/images/piv-mysqlweb1.png)

<h3>Run stand alone outside of PCF</h3>

- $ git clone https://github.com/papicella/PivotalMySQLWeb.git
- $ cd PivotalMySQLWeb
- $ mvn package
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

<h3>Screen Shots</h3>

<hr />
Pas Apicella [papicella at pivotal.io] is a Senior Platform Architect at Pivotal Australia 