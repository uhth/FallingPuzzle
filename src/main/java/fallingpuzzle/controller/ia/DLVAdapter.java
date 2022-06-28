package fallingpuzzle.controller.ia;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import fallingpuzzle.model.Row;
import fallingpuzzle.model.Tile;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DLVAdapter
{

    private File file;

    //MAIN BUILDING CHAIN
    public String createProgram(final ObservableList<Node> rows)
    {
        final StringBuilder sb = new StringBuilder();

        //FACTS
        for (int i = 0; i < rows.size(); ++i)
        {
            final Row row = (Row) rows.get(i);
            sb.append(convertRowIntoRules(row, i));
        }

        sb.append(createIndexes(rows.size() - 1)); //indexes

        //RULES
        sb.append(createTileMoveRule()); //guess
        sb.append(createTileSizeRule());
        sb.append(createTileFallRule());
        sb.append(createOccupiedIndexesRow());

        //STRONG CONSTRAINTS
        sb.append(createStrongConstraints());

        //QUERY
        //	sb.append( createQuery() );

        return sb.toString();
    }

    public File getFile()
    {
        return file;
    }

    public void streamGridIntoProcess(final ObservableList<Node> rows, final Process process)
    {
        try
        {
            final OutputStream out = process.getOutputStream();
            final OutputStreamWriter ow = new OutputStreamWriter(out);
            final String program = createProgram(rows);
            ow.write(program);
            ow.close();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    //Maps tile indexes ( first index, every index, row ) GENS FACTS
    private String convertRowIntoRules(final Row row, final int rowIndex)
    {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.getChildren().size(); ++i)
        {
            final Tile tile = (Tile) row.getChildren().get(i);
            for (int j = 0; j < tile.getIndexes().size(); ++j)
            {
                sb.append("\ntile(" + tile.getFirstIndex() + ", " + tile.getIndexes().get(j) + ", " + rowIndex + ").");
            }
        }
        return sb.toString();
    }

    //Create indexes
    private String createIndexes(final int size)
    {
        return "\n" + "index(0.." + size + ").";
    }

    //create occupiedIndexesRow rule
    private String createOccupiedIndexesRow()
    {
        return "\n" + "occupiedIndexesRow( I, R ) :- tile( X, I, R ).";
    }

    //create query tileMove( X, Y, R )?
    @SuppressWarnings("unused")
    private String createQuery()
    {
        return "\n" + "tileMove( X, Y, R )?";
    }

    //Create strong constraints
    private String createStrongConstraints()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n" + "nTileMoves( S ) :- #count{ X, Y, R : tileMove( X, Y, R ) } = S.");
        sb.append("\n" + ":- nTileMoves( S ), S != 1."); //shall be 1 move

        sb.append("\n" + ":- tileMove( X, Y, R )," //no tiles already there
                + " tile( Z, Y, R ).");

        sb.append("\n" + ":- tileMove( X, Y, R )," //no tiles in between <- left
                + " tile( Z, K, R )," + " K > Y," + " K < X," + " Y < X.");

        sb.append("\n" + ":- tileMove( X, Y, R )," //no tiles in between -> right
                + " tile( Z, K, R )," + " K < Y," + " K > X," + " Y > X.");

        sb.append("\n" + ":- tileMove( X, Y, R )," //no tiles colliding -> right
                + " tile( Z, K, R )," + " tileSize( X, S, R )," + " L = Y + S," + " K > Y," + " K < L," + " Y > X.");

        return sb.toString();
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

}
