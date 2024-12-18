package fr.istic.vv_tp2.ex5;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

/**
 * Visitor used to compute cyclomatic complexities of all methods from a
 * compilation unit.
 */
public class CyclomaticUnitVisitor extends
        VoidVisitorAdapter<CyclomaticUnitVisitor.CycloUnitParam> {

    @Override
    public void visit(CompilationUnit unit, CycloUnitParam param) {
        // Get the package name.
        param.packageName = unit.getPackageDeclaration()
                .map(NodeWithName::getNameAsString)
                .orElse("(default)");

        // Iterate over unit's class declarations.
        for (ClassOrInterfaceDeclaration intClass : unit.findAll(
                ClassOrInterfaceDeclaration.class))
            intClass.accept(this, param);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration intClass, CycloUnitParam param) {
        // Filtering interfaces.
        if (intClass.isInterface())
            return;

        String className = intClass.getNameAsString();

        // Iterate over all methods of the class.
        for (MethodDeclaration method : intClass.getMethods()) {
            if (method.getBody()
                    .isEmpty())
                continue;

            String methodName = method.getNameAsString();

            // Build the list of parameters.
            List<Parameter> params = method.getParameters();
            List<String> paramStrings =
                    params.stream()
                            .map(p -> p.getTypeAsString() + " " + p.getNameAsString())
                            .toList();
            String paramList = "\"(" + String.join(", ", paramStrings) + ")\"";

            // Compute the cyclomatic complexity.
            int cyclomaticComplexity = calculateCyclomaticComplexity(method);

            // Add the entry.
            param.cycloEntries.add(
                    new CyclomaticComplexity.CycloEntry(param.packageName, className, methodName,
                            paramList,
                            cyclomaticComplexity));

            // Explore subclasses.
            for (FieldDeclaration field : intClass.getFields())
                field.ifClassOrInterfaceDeclaration(cls -> cls.accept(this, param));
        }
    }

    /**
     * Calculates the cyclomatic complexity of a method.
     *
     * @param declaration AST node of the declaration of the method to evaluate.
     * @return The cyclomatic number of the method declared by <code>declaration</code>.
     * @see CyclomaticComplexityVisitor
     */
    public int calculateCyclomaticComplexity(MethodDeclaration declaration) {
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        CyclomaticComplexityVisitor.CycloCounter counter =
                new CyclomaticComplexityVisitor.CycloCounter();

        declaration.accept(visitor, counter);

        return counter.getCyclomaticNumber();
    }

    /**
     * Parameter of this visitor's methods.
     */
    public static class CycloUnitParam {
        /**
         * Name of the current package.
         */
        String packageName;

        /**
         * List of cyclomatic entries to expand.
         */
        final List<CyclomaticComplexity.CycloEntry> cycloEntries;

        /**
         * Constructor of the class.
         *
         * @param cycloEntries List of cyclomatic entries to expand.
         */
        public CycloUnitParam(List<CyclomaticComplexity.CycloEntry> cycloEntries) {
            this.cycloEntries = cycloEntries;
        }
    }

}
