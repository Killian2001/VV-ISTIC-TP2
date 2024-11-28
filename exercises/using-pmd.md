# Using PMD

Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset (see the [pmd install instruction](./pmd-help.md)). Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false positive). Explain why you would not solve this issue.

## Answer

We work on the Apache Common Collections library:
https://github.com/apache/commons-collections

We work on the commit `d083fa592997ed9bca14cb1f47a80afe6d33ae4a`,
submitted the `Fri Nov 22 20:44:52 2024 -0500`.

To run PMD, we use the `maven-pmd-plugin-default.xml` ruleset :
https://github.com/apache/maven-pmd-plugin/blob/master/src/main/resources/rulesets/java/maven-pmd-plugin-default.xml

From the directory containing the Common Collections folder, we run :

```bash
curl https://raw.githubusercontent.com/apache/maven-pmd-plugin/refs/heads/master/src/main/resources/rulesets/java/maven-pmd-plugin-default.xml -o maven-pmd-plugin-default.xml

pmd check -R ruleset/maven-pmd-plugin-default.xml -d commons-lang/ -r pmd_report_lang
```

The `-R` option allows us to select the ruleset, the `-d` option allows us to select the source
folder, and `-r` the output file, conataining the report.

The report can be found [here](../pmd_report_lang).

One example of true positive examples found by the analyzer is :

```
.\commons-lang\src\test\java\org\apache\commons\lang3\util\FluentBitSetTest.java:740:	EmptyCatchBlock:	Avoid empty catch blocks
.\commons-lang\src\test\java\org\apache\commons\lang3\util\FluentBitSetTest.java:747:	EmptyCatchBlock:	Avoid empty catch blocks
.\commons-lang\src\test\java\org\apache\commons\lang3\util\FluentBitSetTest.java:754:	EmptyCatchBlock:	Avoid empty catch blocks
.\commons-lang\src\test\java\org\apache\commons\lang3\util\FluentBitSetTest.java:1137:	EmptyCatchBlock:	Avoid empty catch blocks
.\commons-lang\src\test\java\org\apache\commons\lang3\util\FluentBitSetTest.java:1202:	EmptyCatchBlock:	Avoid empty catch blocks
.\commons-lang\src\test\java\org\apache\commons\lang3\util\FluentBitSetTest.java:1442:	EmptyCatchBlock:	Avoid empty catch blocks
.\commons-lang\src\test\java\org\apache\commons\lang3\util\FluentBitSetTest.java:1449:	EmptyCatchBlock:	Avoid empty catch blocks
.\commons-lang\src\test\java\org\apache\commons\lang3\util\FluentBitSetTest.java:1459:	EmptyCatchBlock:	Avoid empty catch blocks
.\commons-lang\src\test\java\org\apache\commons\lang3\util\FluentBitSetTest.java:1588:	EmptyCatchBlock:	Avoid empty catch blocks
.\commons-lang\src\test\java\org\apache\commons\lang3\util\FluentBitSetTest.java:1595:	EmptyCatchBlock:	Avoid empty catch blocks
.\commons-lang\src\test\java\org\apache\commons\lang3\util\FluentBitSetTest.java:1605:	EmptyCatchBlock:	Avoid empty catch blocks
.\commons-lang\src\test\java\org\apache\commons\lang3\util\FluentBitSetTest.java:1624:	EmptyCatchBlock:	Avoid empty catch blocks
.\commons-lang\src\test\java\org\apache\commons\lang3\util\FluentBitSetTest.java:1667:	EmptyCatchBlock:	Avoid empty catch blocks
```

This warning is related to the use of empty catch blocks in test methods of the `FluentBitSetTest` 
class. An example of such method is :

```java
@Test
public void test_flipII() {
    
    /* ... a lot of tests */

    /* PMD emits warnings for the following try-catch blocks */

    // test illegal args
    bs = newInstance(10);
    try {
        bs.flip(-1, 3);
        fail("Test1: Attempt to flip with  negative index failed to generate exception");
    } catch (final IndexOutOfBoundsException e) {
        // correct behavior
    }

    try {
        bs.flip(2, -1);
        fail("Test2: Attempt to flip with negative index failed to generate exception");
    } catch (final IndexOutOfBoundsException e) {
        // correct behavior
    }

    try {
        bs.flip(4, 2);
        fail("Test4: Attempt to flip with illegal args failed to generate exception");
    } catch (final IndexOutOfBoundsException e) {
        // correct behavior
    }
}
```

Althrough this is not spotted by PMD, a first commentary we can do about it
is that it covers a dozen of test cases, where good practices
requires to use one method per test case. Thus, a first fix we can do is to split
method between several test cases.

The PMD warning spot the use of empty catch blocks in the test method.
The idea behind this code is that the raise of an exception will prevent
the execution to reach the fail method, leading to a failed test. The empty
catch block help the execution to continue to other test cases.

After splitting the method in several method for each test case, it will
be preferable to use the `assertThrow` method of JUnit, checking if an exception
of a given class is thrown. For the first case, with `bs.flip(-1, 3)`, we could
write the following method :

```java
@Test
public void testFlip1() {
    final FluentBitSet bs = newInstance();
    assertThrows(IndexOutOfBoundsException.class, () -> bs.flip(-1, 3));
}
```

The result is more concise and readable, respects the rule of 1 case -> 1 method, 
and approriately uses JUnit 5 features.

Still for Commons Lang, we can find a particularly clear case of false positive
in a test file, the `StringUtilsTest` class :

```
.\commons-lang\src\test\java\org\apache\commons\lang3\StringUtilsTest.java:2612:	AvoidUsingHardCodedIP:	Do not hard code the IP address ${variableName}
```

The so-called hardcoded IP looks like this in the file :

```java
// test delimiter char with max
input = "1::2::3:4";
expected = new String[]{"1", "", "2", ":3:4"};
```

This string can be found in a test case used to test a split method : it is
obvious that these strings are juste used for test the split method. However,
PMD spots these strings, because they form a valid IPv6 address string. Even if
it is effectively a bad practice to include hardcoded IP addresses in code,
it is clear here that the goal is just to test a method.


