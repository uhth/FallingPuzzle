package fallingpuzzle.model;

import java.util.Random;

import javafx.scene.paint.Color;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TileGenerator
{
    Random random = new Random();

    public void genTiles(final Row row)
    {

        final double tileHeight = row.getHeight() >= 73 ? row.getHeight() : 73;
        while (row.getChildren().size() < 3)
        {
            int index = 0;
            int size = 0;
            Tile tile;
            do
            {
                index = random.nextInt(6);
                size = random.nextInt(3) + 1;
                tile = new Tile(index, size, row.getWidth() / 8, tileHeight);
                log.info("index: {} size: {} collides: {}", index, size, row.collidesWithOtherTiles(tile));
            }
            while (row.collidesWithOtherTiles(tile));

            if (tile.getNCell() == 1)
            {
                tile.setFill(Color.BLACK);
            }
            else if (tile.getNCell() == 2)
            {
                tile.setFill(Color.RED);
            }
            else
            {
                tile.setFill(Color.BLUE);
            }

            row.getChildren().add(tile);

        }
    }

}
