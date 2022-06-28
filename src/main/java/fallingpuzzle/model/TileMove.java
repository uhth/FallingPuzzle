package fallingpuzzle.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TileMove
{
    Tile tile;

    Integer newIndex;

    Integer rowIndex;
}
