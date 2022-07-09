package fallingpuzzle.controller.ia;

import fallingpuzzle.model.Row;
import fallingpuzzle.model.Tile;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public interface DVLProgram
{
    //MAIN BUILDING CHAIN
    public default String createProgram(final ObservableList<Node> rows)
    {
        final StringBuilder program = new StringBuilder();

        //FACTS
        genFacts(rows, program);
        program.append(createIndexes(rows.size() - 1)); //indexes

        //RULES
        program.append(createTileMoveRule()); //guess
        program.append(createTileSizeRule());
        program.append(createTileFallRule());

        //STRONG CONSTRAINTS
        program.append(createStrongConstraints());

        return program.toString();
    }

    //Maps tile indexes ( first index, every index, row ) GENS FACTS
    private String convertRowIntoRules(final Row row, final int rowIndex)
    {
        final StringBuilder program = new StringBuilder();
        for (int i = 0; i < row.getChildren().size(); ++i)
        {
            final Tile tile = (Tile) row.getChildren().get(i);
            for (int j = 0; j < tile.getIndexes().size(); ++j)
            {
                program.append("\ntile(" + tile.getFirstIndex() + ", " + tile.getIndexes().get(j) + ", " + rowIndex + ").");
            }
        }
        return program.toString();
    }

    //Create indexes
    private String createIndexes(final int size)
    {
        return "\n" + "index(0.." + size + ").";
    }

    //Create strong constraints
    private String createStrongConstraints()
    {
        final StringBuilder program = new StringBuilder();
        program.append("\n" + "nTileMoves( S ) :- #count{ X, Y, R : tileMove( X, Y, R ) } = S.");
        program.append("\n" + ":- nTileMoves( S ), S != 1."); //shall be 1 move

        program.append("\n" + ":- tileMove( X, Y, R )," //no tiles already there
                + " tile( Z, Y, R ), Z!=X.");

        program.append("\n" + ":- tileMove( X, Y, R )," //no tiles in between <- left
                + " tile( Z, K, R )," + " K > Y," + " K < X," + " Y < X.");

        program.append("\n" + ":- tileMove( X, Y, R )," //no tiles in between -> right
                + " tile( Z, K, R )," + " K < Y," + " K > X," + " Y > X.");

        program.append("\n" + ":- tileMove( X, Y, R )," //no tiles in between -> right
                + " tile( Z, K, R )," + " K < Y," + " K > X," + " Y > X.");

        return program.toString();
    }

    //Create tailFall rule <-- TODO
    private String createTileFallRule()
    {
        return "\n" + "";
    }

    //Create tailFall rule <-- TODO  - tileMove( firstIndex, newIndex, row );
    private String createTileMoveRule()
    {
        return "\n" + "tileMove( X, Y, R ) | nTileMove( X, Y, R ) :-" + " tile( X, _, R )," + " index( Y )," + " X != Y.";
    }

    //Create tileSize rule - tileSize( firstIndex, #indexes, row )
    private String createTileSizeRule()
    {
        return "\n" + "tileSize( X, Size, R ) :- tile( X, Y, R ), #count{ X, I : tile( X, I, R ) } = Size.";
    }

    private void genFacts(final ObservableList<Node> rows, final StringBuilder program)
    {
        //FACTS
        for (int i = 0; i < rows.size(); ++i)
        {
            final Row row = (Row) rows.get(i);
            program.append(convertRowIntoRules(row, i));
        }
    }

}
