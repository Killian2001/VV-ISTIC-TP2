# Code of your exercise

- [XPATH ruleset](../../ruleset/my-ruleset.xml)
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <ruleset name="My Ruleset"
        xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd">
        <description>
            My custom ruleset, with the 3-imbricated if statement rule.
        </description>
        
        <rule name="TooMuchNestedIf"
            language="java"
            message="More than 3 if statements directly or undirectly nested." 
            class="net.sourceforge.pmd.lang.rule.xpath.XPathRule" >
            <description>
                There is more than 3 if statements which are directly or undirectly nested. 
                This discouraged as it reduce lisibility of the code.
            </description>
            <priority>3</priority> <!-- Non critical error, low priority -->
            <properties>
                <property name="xpath">
                    <value>
                        <![CDATA[
                            //IfStatement//IfStatement//IfStatement
                        ]]>
                    </value>
                </property>
            </properties>
        </rule>
    </ruleset>
    ```

- [Test class](Test.java)
    ```java
    public class Test {

        public static void main(String[] args) {
            if (true)
                if (true)
                    /* Too much imbricated if statements from here! */
                    if (true)
                        System.out.println("Hello World");
        }

        public void foo() {
            boolean cond = true;
            boolean otherCond = false;

            boolean a = true;
            boolean b = true;

            if (cond) {
                while(true) {
                    if (!otherCond) {
                        for (int i = 0; i < 100; i++) {
                            /* Too much imbricated if statements from here! */
                            if (a || b) {
                                if (a && b) {
                                    System.out.println("ok");
                                }
                            }
                        }
                    }
                }
            }
        }

    }
    ```