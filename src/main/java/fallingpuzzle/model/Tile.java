package fallingpuzzle.model;

import java.util.ArrayList;

import fallingpuzzle.controller.tile.TileDragController;
import fallingpuzzle.controller.tile.TileSelectController;
import fallingpuzzle.model.utils.IndexChangeListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {
	
	private ArrayList<Integer> indexes; 
	private IntegerProperty firstIndex;
	private int nCell;
	private Row row;
	private TileSelectController tileSelectController;
	private TileDragController tileDragController;
	private double baseWidth = 0;
	private double baseHeight = 0;
			
	public Tile( int firstIndex, int nCell, double baseWidth, double baseHeight ) {
		
		tileSelectController = new TileSelectController( this, false );
		tileDragController = new TileDragController( this );
		tileDragController.isDraggableProperty().set( false );
		this.indexes = new ArrayList<Integer>();
		this.firstIndex = new SimpleIntegerProperty( 10 );
		
		
		//Updating first index will also update indexList
		this.firstIndex.addListener( new IndexChangeListener( this ) );
		
		this.nCell = nCell;
		this.baseHeight = baseHeight;
		this.baseWidth = baseWidth;
		resize();
		
		this.firstIndex.set( firstIndex );
	}
	
	//Used by listener to update tile indexes as the first index changes
	public void updateIndexes( Number newValue ) {
		indexes.clear();
		for( int i = 0; i < nCell; ++i )
			indexes.add( newValue.intValue() + i );		
	}
	
	public int getNCell() {
		return nCell;
	}
	
	public void setNCell( int nCell ) {
		this.nCell = nCell;
		updateIndexes( firstIndex.getValue() );
		resize();
	}
	
	public void setSelectable( boolean selectable ) {
		tileSelectController.setSelectable( selectable );
	}
	
	public void setDraggable( boolean draggable ) {
		tileDragController.isDraggableProperty().set( draggable );
	}
		
	public Tile( int firstIndex, int nCell ) {
		this( firstIndex, nCell, 0.0, 0.0 );
	}
	
	public ArrayList<Integer> getIndexes() {
		return indexes;
	}
	
	public Integer getFirstIndex() {
		return firstIndex.get();
	}
	
	public Row getRow() {
		return row;
	}
	
	public void setRow( Row row ) {
		this.row = row;
	}
	
	public void move( int index ) {
		this.firstIndex.set( index );
	}
		
	private void resize() {
		this.setWidth( ( baseWidth * nCell ) - 2 );
		this.setHeight( baseHeight - 2 );
	}

	public void updateTileSize( double baseWidth, double baseHeight ) {
		this.baseWidth = baseWidth;
		this.baseHeight = baseHeight;
		resize();
	}

}
