# Cyclomatic Complexity with JavaParser

With the help of JavaParser implement a program that computes the Cyclomatic Complexity (CC) of all methods in a given Java project. The program should take as input the path to the source code of the project. It should produce a report in the format of your choice (TXT, CSV, Markdown, HTML, etc.) containing a table showing for each method: the package and name of the declaring class, the name of the method, the types of the parameters and the value of CC.
Your application should also produce a histogram showing the distribution of CC values in the project. Compare the histogram of two or more projects.


Include in this repository the code of your application. Remove all unnecessary files like compiled binaries. Do include the reports and plots you obtained from different projects. See the [instructions](../sujet.md) for suggestions on the projects to use.

You may use [javaparser-starter](../code/javaparser-starter) as a starting point.

## Answers

### Functionalities

The cyclomatic complexity analyzer includes the following functionalities :
- a cyclomatic complexity calculator
- CSV export
- PNG bar plot export using [JFreeChart](https://www.jfree.org/jfreechart/)

Reports are included at the root of the exercise 5 code folder.

### Implementation details

The program doesn't computes the cyclomatic complexity by reconstructing the entire control flow graph 
; instead, it calculates it by exploring each method code using the `CyclomaticComplexityVisitor`, 
which, for each control-flow modifying statement, calculates the number of vertices (= basic blocks of 
the program, i.e. sequences of non-branching statements) and the number of edges (= branchs) related to 
the statement. Final number is calculated when all statements of the method had been explored, using 
the cyclomatic complexity formula :

$$CC = E - N + 2$$

under the assumption that the control-flow graph of the method is connected.

For each compilation unit, the visitor `CyclomaticUnitVisitor` keep tracks of each method cyclomatic complexity which is computed. Results are stored in the forms of records in a list, shared between all visitors executed by the program.

Export is done by two classes, which implement both the `CycloExporter` interface : the class `CSVCycloExporter` export computed complexities in a CSV file, in descending order ; the class `BarplotCycloExporter` creates a bar plot from computed complexities, also order by descending complexities. Each `CycloExporter` use the cyclomatic complexity records list to produce the export.

### Experiments over Apache libraries.

We ran the cyclomatic complexity calculator over the following Apache libraries :

- Apache Commons CLI
- Apache Commons Collections
- Apache Commons Lang
- Apache Commons Math

The results are the following :

- For Apache Common CLI :
