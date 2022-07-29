# DbUnit

The dbunit module provides an integration of the [DbUnit](http://dbunit.sourceforge.net/) JUnit extension. Furthermore it contains base 
classes for test cases that can be used to setup a Database from an Excel or an XML file before the actual test run. 

Check the [DbUnit](http://dbunit.sourceforge.net/howto.html) documentation for an overview on how to use DbUnit. 

If you want to use the `girders-dbunit` provided functionality for easy database setup from an Excel or XML file, have 
your test case extend from `AbstractExcelDbTestCase` or `AbstractXmlDbTestCase` respectively. This will load the 
dataset found in the Excel/XML file named after the test class with a `-db.xls` (for Excel) or `-db.xml` (for XML) 
suffix from the class path (using the defining class loader of the test class). E.g: The dataset for the test class `FooTest` 
is expected to reside in `FooTest-db.xls` (for Excel) or `FooTest-db.xml` (for XML) on the class path. For this to 
work a `javax.sql.DataSource` must exist in the application context (usually provided by auto-configuration) that can 
be autowired into `AbstractDbTestCase`. A test case then looks as follows:

    @DataJpaTest
    @ImportAutoConfiguration(DbUnitAutoConfiguration.class)
    @ExtendWith(SpringExtension.class)
    public class SampleExcelDbTest extends AbstractExcelDbTestCase {
      // test code relying on a database properly set up
    }
    
      
To run your tests against a h2 in memory Database you would define the following dependencies:       
      
    <dependency>
      <groupId>com.netcetera.girders</groupId>
      <artifactId>girders-starter-dbunit</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
      <scope>test</scope>
    </dependency>

The provided base classes internally uses an implementation of `org.dbunit.IDatabaseTester` that returns a database vendor 
specific connection adapter. Those adapters are provided by DbUnit under the package `org.dbunit.ext` (E.g. `org.dbunit.ext.h2.H2Connection` 
for the H2 database). The connection adapter to use can be configured with the following property: `dbunit.connectionAdapter`.
If you use auto-configuration the Maven dependencies mentioned above are sufficient and nothing needs to be configured.
If no H2 driver is on the classpath or if you want to use dbUnit with a different database, you need to set the property 
for the connection adapter.

## Configuration Properties

The following properties are relevant for the configuration of the Girders dbUnit module:

| Property | Default | Description |
|:---------|:--------|:------------|
| girders.dbunit.connectionAdapter | n/a | The vendor specific connection adapter used by DbUnit (e.g. `org.dbunit.ext.h2.H2Connection`). |
| girders.dbunit.schema | n/a | The schema to use for DB connections created by DbUnit (optional) |


## Data Insertion Order

For `-db.xls` files, the data is inserted in the order in which the spreadsheets appear in the file.


For `-db.xml` files, the data is inserted per-table in the order defined by the first element of each type appearing in the
file. In other words: if the first element of type `A` appears before the first element of type `B` in the `-db.xml`, then all
elements of type `A` in the `-db.xml` will be inserted into table `A` in the DB before all elements of type `B` are inserted
into table `B` in the DB. The following example illustrates this:

    <?xml version="1.0" encoding="UTF-8"?>
    <dataset>
      <A id="1" />
      <B id="111" />
      <A id="2" />
    </dataset>

In this example, the row of table `A` with `id = 2` would be inserted in the DB before the row of table `B` would be
inserted. To change the order of tables, you can define empty rows in the order in which you want the tables to be
processed, like this:

    <?xml version="1.0" encoding="UTF-8"?>
    <dataset>
      <B />
      <A />
    
      <A id="1" />
      <A id="2" />
      <B id="111" />
      <B id="222" />
    </dataset>
    
Now table `B` will have both its rows inserted before table `A` will have any rows inserted.

## Parallelized Tests

#### Basic setup

The classes `AbstractParallelizedExcelDbTestCase` and `AbstractParallelizedXmlDbTestCase` allow for parallelized DB
tests. As with the sequential versions `AbstractExcelDbTestCase` and `AbstractXmlDbTestCase`, you only need to extend
your test classes from the respective abstract class. [JUnit 5 parallelization needs to be enabled](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parallel-execution-synchronization) to make
use of the parallelization functionality.

In contrast to the sequential tests, the parallelized tests perform no database teardown at the beginning of each test
for concurrency reasons. Instead, for a given test class the `-db.xls`/`-db.xml` is `INSERT`ed into the DB exactly once
before any test cases of the class are run, after which DbUnit does not interact with the DB from that test class again.
This leads to the following requirements:

- the test data for a given test case is fully contained within the data set files (see below) used for that test class
- the data for each test case is independent of the data for all other test cases so test cases can execute
  independently regardless of scheduling (at an absolute minimum, no test case can modify any data that any other test
  case relies on)
- each data set is self-contained (i.e. loading any one of them on its own never produces any constraint violations)
- the union of all data sets that are loaded in parallel does not contain any constraint violations

A parallelized test class that would use files `ParallelTests-db.xml` and `ParallelTests-update-db.xml`
(regarding the `-update-db.xml`, see below) located in the same package as the test class would look like this:

    @DataJpaTest
    @ImportAutoConfiguration(DbUnitAutoConfiguration.class)
    @ExtendWith(SpringExtension.class)
    public class ParallelTests extends AbstractParallelizedXmlDbTestCase {
    
      @Test
      void testCaseA() {
      }
      
      @Test
      void testCaseB() {
      }
      
      // more test cases relying on the data in ParallelTests-db.xml and ParallelTests-update-db.xml
    }
Test cases `testCaseA` and `testCaseB` would be executed concurrently after the data set has been loaded into the DB.

#### Updating data sets

An additional feature of the parallelized tests is that in addition to the required `-db.xls`/`-db.xml` file that a test
class uses, you can optionally supply an `-update-db.xls`/`-update-db.xml` file for the test class that will be used to
`UPDATE` the data immediately after it was loaded from the `-db.xls`/`-db.xml` file. The `-update-db.xls`/`-update-db.xml`
file cannot contain any new data rows, only rows that exist in the `-db.xls`/`-db.xml` can be `UPDATE`ed. This can be
changed by extending `ParallelizedDataManagementProvider`, overriding the `getUpdateOperation` method, and using the
extending class as a constructor argument for `AbstractDbTestCase(DataManagementProvider, DataSetProvider)`. An example
using the `DatabaseOperation.REFRESH` operation instead of `UPDATE` for the `-update-db.xml` would look like this:

    @DataJpaTest
    @ImportAutoConfiguration(DbUnitAutoConfiguration.class)
    @ExtendWith(SpringExtension.class)
    public class ParallelTests extends AbstractDbTestCase {
    
      ParallelTests() {
        super(new ParallelizedDataManagementProvider() {
                @Override
                public DatabaseOperation getUpdateOperation() {
                  return DatabaseOperation.REFRESH;
                }
              },
              new FlatXmlDataSetProvider()
        );
      }
      
      // test code relying on a database properly set up
    }
For a use case of using an `-update-db.*` file: this makes it is possible to insert a data set into the DB that contains
cycles of foreign key references (e.g. `tableA.rowA.columnA` could refrence `tableB.rowB.columnB` while
`tableB.rowB.columnC` could in return reference `tableA.rowA.columnD`; longer cycles are possible too) if at least one
of the columns involved in the cycle is nullable. To insert the data set, insert all data rows via the `-db.*` file,
leaving out at least one link in the cycle, then update the data using the `-update-db.*` file.

For an example, consider a table `A` with columns `id` and `id_b`, and a table `B` with columns `id` and `id_a`. Column
`id` is the primary key in each table, and the other column is a nullable foreign key referencing the other table. To
insert a data set with a foreign key cycle, you could use this `TestClass-db.xml`:

    <?xml version="1.0" encoding="UTF-8"?>
    <dataset>
    
      <A id="1" />
      <B id="1000" id_a="1"/>
    </dataset>

and this `TestClass-update-db.xml`:

    <?xml version="1.0" encoding="UTF-8"?>
    <dataset>
    
      <A id="1" id_b="1000"/>
    </dataset>
    
Together, these files result in one row each in table `A` and `B` that reference each other.

Note that all elements for the same table in the `-update-db.*` file need to have the same columns, otherwise rows of
elements that are missing a column that exists in some other element of the same type in the file will have that column
set to `NULL`. For example, if this is the `-db.xml` for a table `C` with primary key `id` and nullable varchar `name`:

    <?xml version="1.0" encoding="UTF-8"?>
    <dataset>
    
      <C id="1" name="foo" />
      <C id="2" />
    </dataset>
 
 and this is the `-update-db.xml`:

    <?xml version="1.0" encoding="UTF-8"?>
    <dataset>
    
      <C id="1" />
      <C id="2" name="updated"/>
    </dataset>

then in the DB, the row of `C` with `id = 1` will first have set the `name = "foo"` during the insert of `-db.xml`, and
then have it set to `name = NULL` during the update via `-update-db.xml` because in the `-update-db.xml`,
element `<C id="1" />` is missing column `name` that is contained in element `<C id="2" name="updated"/>`.
