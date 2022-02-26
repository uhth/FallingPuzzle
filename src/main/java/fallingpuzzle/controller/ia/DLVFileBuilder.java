package fallingpuzzle.controller.ia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import fallingpuzzle.model.Row;
import fallingpuzzle.model.Tile;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public class DLVFileBuilder {
	
	private File file;
	
	public File getFile() {
		return file;
	}
	
	public void createFile( ObservableList<Node> rows ) {
		try {
			file = File.createTempFile( "file", null );
			@SuppressWarnings("resource")
			OutputStreamWriter ow = new OutputStreamWriter( new FileOutputStream( file ) );
			ow.write( createProgram( rows ) );
			ow.flush();
		}
		catch( IOException e ) {
			
		}
	}
	
	//MAIN BUILDING CHAIN
	public String createProgram( ObservableList<Node> rows ) {
		StringBuilder sb = new StringBuilder();
		
		//FACTS
		for( int i = 0; i < rows.size(); ++i ) {
			Row row = ( Row ) rows.get( i );
			sb.append( convertRowIntoRules( row, i ) );
		}
		
		sb.append( createIndexes() ); //indexes
		
		//RULES
		sb.append( createTileMoveRule() ); //guess
		sb.append( createTileSizeRule() ); 
		sb.append( createTileFallRule() );
		sb.append( createOccupiedIndexesRow() );
		
		//STRONG CONSTRAINTS
		sb.append( createStrongConstraints() );
		
		//QUERY
		sb.append( createQuery() );
		
		
		return sb.toString();
	}
	
	//Maps tile indexes ( first index, every index, row ) GENS FACTS
	private String convertRowIntoRules( Row row, int rowIndex ) {
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < row.getChildren().size(); ++i ) {
			Tile tile = ( Tile ) row.getChildren().get( i );
			for( int j = 0; j < tile.getIndexes().size(); ++j ) {
				sb.append( "\ntile(" + tile.getFirstIndex() + ", " + tile.getIndexes().get( j ) + ", " + rowIndex + ")." );
			}
		}		
		return sb.toString();
	}
	
	//Create indexes
	private String createIndexes() {
		return "\n" + "index(0..7).";
	}
	
	//Create tileSize rule - tileSize( firstIndex, #indexes, row )
	private String createTileSizeRule() {
		return "\n" + "tileSize( X, Size, R ) :- tile( X, Y, R ), #count{ X, I : tile( X, I, R ) } = Size.";
	}
	
	//Create tailFall rule <-- TODO
	private String createTileFallRule() {
		return "\n" + "";
	}
	
	//create occupiedIndexesRow rule
	private String createOccupiedIndexesRow() {
		return "\n" + "occupiedIndexesRow( I, R ) :- tile( X, I, R ).";
	}
	
	//Create tailFall rule <-- TODO  - tileMove( firstIndex, newIndex, row );
	private String createTileMoveRule() {
		return "\n" + "tileMove( X, Y, R ) | nTileMove( X, Y, R ) :-"
				+ " tile( X, _, R ),"
				+ " index( X ),"
				+ " index( Y ),"
				+ " X != Y.";
				/*
				+ " occupiedIndexesRow( I, R ),"
				+ " tileSize( X, S, R ),"
				+ "	K = Y + S,"
				+ " I < Y,"
				+ " I > K.";
				*/
	}
		
	//Create strong constraints
	private String createStrongConstraints() {
		StringBuilder sb = new StringBuilder();
		sb.append( "\n" + "nTileMoves( S ):- #count{ X, Y, R : tileMove( X, Y, R ) } = S." );
		sb.append( "\n" + ":- nTileMoves( S ), S != 1." ); //shall be only 1 move
		
		sb.append( "\n" + ":- tileMove( X, Y, R ), occupiedIndexes( I, R ), Y == R, Y == K.");
		
		return sb.toString();
	}
	
	//create query tileMove( X, Y, R )?
	private String createQuery() {
		return "\n" + "tileMove( X, Y, R )?";
	}
	
}