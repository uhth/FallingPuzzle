package fallingpuzzle.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import fallingpuzzle.controller.scene.GameController;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Row extends Pane {
		
	private GameController gameController;
	
	public Row() {}
	
	public void setController( GameController gameController ) {
		this.gameController = gameController;
	}
	
	@Override
	public boolean equals( Object row ) {
		if( row instanceof Row )
			return this.getChildren().containsAll( ( (Row ) row).getChildren() );
		return false;
	}
	
	public boolean isFull() {
		int indexCount = 0;
		for( int i = 0; i < getChildren().size(); ++i ) {
			indexCount += (( Tile ) getChildren().get( i ) ).getIndexes().size();
		}
		if( indexCount == 8 ) return true;
		return false;
	}
	
	public void fitToParent() {
		VBox parent = ( VBox ) this.getParent();
		this.setMinWidth( parent.getWidth() );
		this.setMaxWidth( parent.getWidth() );
		this.setMaxHeight( parent.getHeight() / 10 );
		this.setMinHeight( parent.getHeight() / 10  );
		this.setWidth( parent.getWidth() );
		this.setHeight( parent.getHeight() / 10 );
		
		for( int i = 0; i < getChildren().size(); ++i ) {
			Tile tile = ( Tile ) getChildren().get( i );
			tile.updateTileSize( this.getWidth() / 8, this.getHeight() );
		}
		
	//	System.out.println( this.getWidth() / 8 );

	}
	
	/* Only inserts tiles which can fit inside this row */
	public void insert( List<Tile> tilesToInsert ) {
		for( int i = 0; i < tilesToInsert.size(); ++i ) {
			Tile tile = ( Tile ) tilesToInsert.get( i );
			if( !collidesWithOtherTiles( tile ) ) { 
				getChildren().add( tile ); 
				tile.setRow( this ); 
			} 
		}
		updateTilesCoords();
	}
	
	/* Only inserts a tile which can fit inside this row */
	public void insert( Tile tileToInsert, boolean checkForCollision ) {
		if( !checkForCollision || !collidesWithOtherTiles( tileToInsert ) ) {
			getChildren().add( tileToInsert );
			tileToInsert.setRow( this );
		}
		updateTilesCoords();
	}
	
	/* Used by controller to move a tile */
	public boolean moveTile( Tile tile, int index ) {
		int oldIndex = tile.getFirstIndex();
		if( tilesInBeetween( tile, index ) ) return false;
		tile.move( index );
		if( collidesWithOtherTiles( tile ) ) {
			tile.move( oldIndex );
			System.out.println("illegal move ");
			System.out.println( "row: " + gameController.getRowPosition( this ) + " move is: " + oldIndex + " to " + index );
			return false;
		}
		else {
			System.out.println( "row: " + gameController.getRowPosition( this ) + " move is: " + oldIndex + " to " + index );
			return true;
		}
	}
		
	//if unavailable indexes contains any index in between mock and real return false;
	public boolean tilesInBeetween( Tile tileToTest, int mockIndex ) {	
		ArrayList<Integer> unavailableIndexes = new ArrayList<Integer>();
		int realIndex = tileToTest.getFirstIndex();
		for( int i = 0; i < getChildren().size(); ++i ) {
			Tile tile = ( Tile ) getChildren().get( i );
			if( !tile.equals( tileToTest ) )
				unavailableIndexes.addAll( tile.getIndexes() );
		}
		unavailableIndexes.sort( new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				int res = ( o1 > o2 ) ? o1 : o2;
				return res;
			}
		});	
		//case mockIndex < realIndex -> test mockIndex + tile size + 1
		if( mockIndex < realIndex )
			for( int i = mockIndex + tileToTest.getIndexes().size() + 1; i < realIndex; ++i ) {
				if( unavailableIndexes.contains( i ) ) return true;
			}
		//case mockIndex > realIndex -> test realIndex + 1
		else if( mockIndex > realIndex )	
			for( int i = realIndex + 1; i < mockIndex; ++i ) {
				if( unavailableIndexes.contains( i ) ) return true;
			}
		
		return false;
	}
	
	public void remove( Tile tile ) {
		getChildren().remove( tile );
	}
	
	/* Updates tile's X for it to be correctly displayed on screen */
	public void updateTilesCoords() {
		for( int i = 0; i < getChildren().size(); ++i ) {
			Tile tile = ( Tile ) getChildren().get( i );
			tile.setX( tile.getFirstIndex() * ( this.getWidth() / 8 ) );
		}
	}
	
	/* Checks for each tile already in if the tested one has any index in common */
	public boolean collidesWithOtherTiles( Tile tileToTest ) {
		ArrayList<Integer> unavailableIndexes = new ArrayList<Integer>();
		for( int i = 0; i < getChildren().size(); ++i ) {
			Tile tile = ( Tile ) getChildren().get( i );
			if( !tile.equals( tileToTest ) )
				unavailableIndexes.addAll( tile.getIndexes() );
		}
				
		for( int i = 0; i < tileToTest.getIndexes().size(); ++i )
			if( unavailableIndexes.contains( tileToTest.getIndexes().get( i ) ) || i < 0 || i > 7 ) {
				return true;
			}
		return false;
	}
	
	/* Checks for each tile already in if the tested one has any index in common if you try to move it */
	public boolean collidesWithOtherTiles( Tile tileToTest, int mockFirstIndex ) {
		ArrayList<Integer> unavailableIndexes = new ArrayList<Integer>();
		for( int i = 0; i < getChildren().size(); ++i ) {
			Tile tile = ( Tile ) getChildren().get( i );
			if( !tile.equals( tileToTest ) )
				unavailableIndexes.addAll( tile.getIndexes() );
		}
		
		int trueFirstIndex = tileToTest.getFirstIndex();
		tileToTest.move( mockFirstIndex );
				
		for( int i = 0; i < tileToTest.getIndexes().size(); ++i )
			if( unavailableIndexes.contains( tileToTest.getIndexes().get( i ) ) || i < 0 || i > 7 ) {
				tileToTest.move( trueFirstIndex );
				return true;
			}
		tileToTest.move( trueFirstIndex );
		return false;
	}
	
	public GameController getGameController() {
		return gameController;
	}
	
	/* get Tile by Index */
	public Tile getTile( int index ) {
		for( int i = 0; i < getChildren().size(); ++i ) {
			Tile tile = ( Tile ) getChildren().get( i );
			if( tile.getFirstIndex() == index ) {
				return tile;
			}
		}
			return null;
	}
	
	
}

