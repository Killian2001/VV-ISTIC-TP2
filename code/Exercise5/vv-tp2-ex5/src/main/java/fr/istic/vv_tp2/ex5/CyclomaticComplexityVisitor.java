package fr.istic.vv_tp2.ex5;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Optional;

/**
 * Visitor used to evaluate the cyclomatic complexity of a method.
 */
public class CyclomaticComplexityVisitor extends
        VoidVisitorAdapter<CyclomaticComplexityVisitor.CycloCounter> {

    /**
     * Statement dispatch method.
     *
     * @param stat    Statement.
     * @param counter Cyclomatic complexity counter.
     */
    private void dispatchStat(Statement stat, CycloCounter counter) {
        stat.ifBreakStmt(s -> s.accept(this, counter));
        stat.ifBreakStmt(s -> s.accept(this, counter));
        stat.ifContinueStmt(s -> s.accept(this, counter));
        stat.ifDoStmt(s -> s.accept(this, counter));
        stat.ifForEachStmt(s -> s.accept(this, counter));
        stat.ifForStmt(s -> s.accept(this, counter));
        stat.ifSwitchStmt(s -> s.accept(this, counter));
        stat.ifTryStmt(s -> s.accept(this, counter));
        stat.ifWhileStmt(s -> s.accept(this, counter));
    }

    @Override
    public void visit(MethodDeclaration method, CycloCounter counter) {
        counter.addVertices(1); // end vertex.

        Optional<BlockStmt> bodyOpt = method.getBody();
        bodyOpt.ifPresent(blockStmt -> blockStmt.accept(this, counter));
    }

    @Override
    public void visit(BlockStmt statement, CycloCounter counter) {
        for (Statement stat : statement.getStatements())
            dispatchStat(stat, counter);
    }

    @Override
    public void visit(BreakStmt statement, CycloCounter counter) {
        counter.addVertices(1);
        counter.addEdges(2);    // input / output.
    }

    @Override
    public void visit(ContinueStmt statement, CycloCounter counter) {
        counter.addVertices(1);
        counter.addEdges(2);    // input / output.
    }

    @Override
    public void visit(DoStmt statement, CycloCounter counter) {
        counter.addVertices(2);
        counter.addEdges(3);

        statement.getBody()
                .accept(this, counter);
    }

    @Override
    public void visit(ForEachStmt statement, CycloCounter counter) {
        counter.addVertices(3);
        counter.addEdges(5);

        statement.getBody()
                .accept(this, counter);
    }

    @Override
    public void visit(ForStmt statement, CycloCounter counter) {
        counter.addVertices(3);
        counter.addEdges(5);

        statement.getBody()
                .accept(this, counter);
    }

    @Override
    public void visit(IfStmt statement, CycloCounter counter) {
        counter.addVertices(2); // add extremities nodes.
        counter.addEdges(3); // if edges + else edge.

        // if block node.
        statement.getThenStmt()
                .accept(this, counter);

        // else block node.
        Optional<Statement> elseStat = statement.getElseStmt();
        if (elseStat.isPresent()) {
            counter.addEdges(1);
            elseStat.get()
                    .accept(this, counter);
        }
    }

    @Override
    public void visit(SwitchStmt statement, CycloCounter counter) {
        counter.addVertices(2); // extremities
        counter.addEdges(1);    // input edge.

        // Iterating over switch statements.
        for (SwitchEntry entry : statement.getEntries()) {
            counter.addEdges(2);    // add input and output edge.
            for (Statement stat : entry.getStatements())
                // does not take account of break, as the output edge is already registered.
                if (!stat.isBreakStmt())
                    dispatchStat(stat, counter);
        }
    }

    @Override
    public void visit(TryStmt statement, CycloCounter counter) {
        counter.addVertices(2); // extremities
        counter.addEdges(2);    // input + end of catch.

        for (CatchClause catchClause : statement.getCatchClauses()) {
            counter.addEdges(2); // input and output edges of the clause.
            catchClause.getBody()
                    .accept(this, counter);
        }

        // get the finally statement.
        Optional<BlockStmt> finallyStat = statement.getFinallyBlock();
        if (finallyStat.isPresent()) {
            counter.addEdges(1);    // output edge of the finally block.
            finallyStat.get()
                    .accept(this, counter);
        }
    }

    @Override
    public void visit(WhileStmt statement, CycloCounter counter) {
        counter.addVertices(3);
        counter.addEdges(5);

        statement.getBody()
                .accept(this, counter);
    }

    /**
     * Cyclomatic complexity counter.
     * Counts the number of vertices and edges in the control flow graph
     * of the method.
     */
    public static class CycloCounter {
        private int vertices = 0;
        private int edges = 0;

        /**
         * Add a given number of vertices.
         *
         * @param v Number of vertices to add.
         */
        public void addVertices(int v) {
            vertices += v;
        }

        /**
         * Add a given number of edges.
         *
         * @param e Number of edges to add.
         */
        public void addEdges(int e) {
            edges += e;
        }

        /**
         * Calculates the cyclomatic number of the method from the number of vertices and edges
         * from the method's control flow graph, under the hypothesis the CFG is connected.
         *
         * @return The cyclomatic complexity of the method <code>edges - vertices + 2</code>
         */
        public int getCyclomaticNumber() {
            return edges - vertices + 2;
        }
    }

}
