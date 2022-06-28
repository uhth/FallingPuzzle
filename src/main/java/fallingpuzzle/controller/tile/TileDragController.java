package fallingpuzzle.controller.tile;

import fallingpuzzle.controller.scene.GameController;
import fallingpuzzle.model.Row;
import fallingpuzzle.model.Tile;
import javafx.scene.Node;

public class TileDragController extends DragController {
	
	
	public TileDragController( Node target ) {
		super( target, true );
	}
	
	@Override
	protected void createHandlers() { 
    	
	    setAnchor = event -> {
	            if ( event.isPrimaryButtonDown() ) {
	                cycleStatus = ACTIVE;
	                anchorX = event.getSceneX();
	                mouseOffsetFromNodeZeroX = event.getX();
	            }
	            if ( event.isSecondaryButtonDown() ) {
	                cycleStatus = INACTIVE;
	                target.setTranslateX(0);
	            }
	        };
        
        updatePositionOnDrag = event -> {
            if (cycleStatus != INACTIVE) {
            	double newX = event.getSceneX() - anchorX;
            	if( !( outSideParentBounds( target.getLayoutBounds(), target.getParent().getLayoutBounds(), target.getLayoutX() + newX ) ) ) {
            			target.setTranslateX( newX );     	
            	}
            }
        };
        
        commitPositionOnRelease = event -> {
        	
            if (cycleStatus != INACTIVE) {
            	
            	double translate = target.getTranslateX();
            	double absTranslate = Math.abs( translate ); 
            	boolean isTranslatePos = ( translate >= 0 ) ? true : false;
            	Tile tile = ( Tile ) target;
            	Row row = ( Row ) tile.getParent();
            	int oldIndex = tile.getFirstIndex();
            	int deltaIndex = 0;
            	            	
        		if( absTranslate >= 35 && absTranslate < 106 ) deltaIndex += 1;
        		else if( absTranslate >= 106 && absTranslate < 177 ) deltaIndex += 2;
        		else if( absTranslate >= 177 && absTranslate < 248 ) deltaIndex += 3;
        		else if( absTranslate >= 248 && absTranslate < 319 ) deltaIndex += 4;
        		else if( absTranslate >= 319 && absTranslate < 390 ) deltaIndex += 5;
        		else if( absTranslate >= 390 && absTranslate < 455 ) deltaIndex += 6;
        		else if( absTranslate >= 455 ) deltaIndex += 7;
        		
        		deltaIndex *= ( isTranslatePos ) ? 1 : -1;
        		oldIndex += deltaIndex;
        		
                target.setTranslateX( 0 );
                
                GameController gameController = ( row ).getGameController();
                
        		if( deltaIndex != 0 )
        			gameController.moveTile( ( Tile ) target, oldIndex );
            }
        };
        
    }
	
}
