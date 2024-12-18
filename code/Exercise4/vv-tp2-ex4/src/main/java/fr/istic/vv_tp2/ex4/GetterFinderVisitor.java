package fr.istic.vv_tp2.ex4;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GetterFinderVisitor extends VoidVisitorAdapter<GetterFinderVisitor.VisitorParam> {

    @Override
    public void visit(CompilationUnit unit, VisitorParam param) {
        param.packageName = unit.getPackageDeclaration()
                .map(NodeWithName::getNameAsString)
                .orElse("(default)");

        for (ClassOrInterfaceDeclaration intClass : unit.findAll(
                ClassOrInterfaceDeclaration.class))
            intClass.accept(this, param);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration intClass, VisitorParam param) {
        // Filter interfaces and non-public classes.
        if (intClass.isInterface() || !intClass.isPublic())
            return;

        List<String> fieldNames = new ArrayList<>();

        // Gathering fields identifiers
        for (FieldDeclaration fieldDecl : intClass.getFields())
            for (VariableDeclarator varDecl : fieldDecl.getVariables())
                if (fieldDecl.isPrivate())
                    fieldNames.add(varDecl.getNameAsString());
                else if (fieldDecl.isClassOrInterfaceDeclaration())
                    fieldDecl.asClassOrInterfaceDeclaration().accept(this, param);

        // For each field, find if they have a getter.
        for (String fieldName : fieldNames) {
            String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) +
                    (fieldName.length() > 1 ? fieldName.substring(1) : "");

            // List of getters.
            List<MethodDeclaration> getters = intClass.getMethodsByName(getterName)
                    .stream()
                    .filter(MethodDeclaration::isPublic)
                    .toList();

            // Print the getter if it is empty.
            if (getters.isEmpty()) {
                String csvLine =
                        String.format("%s,%s,%s", fieldName, intClass.getNameAsString(),
                                param.packageName);
                param.output.println(csvLine);
            }
        }
    }

    public static class VisitorParam {
        final PrintWriter output;
        String packageName;

        public VisitorParam(PrintWriter output) {
            this.output = output;
        }
    }

}
