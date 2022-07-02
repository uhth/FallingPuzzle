package fallingpuzzle.model;

import java.util.Random;

public class TileGenerator
{
    Random random = new Random();

    public void genTiles(final Row row)
    {
        int counter0 = 0;
        final double tileHeight = row.getHeight() >= 73 ? row.getHeight() : 73;
        while (row.getChildren().size() < 3 && counter0++ <= 10)
        {
            int index = 0;
            int size = 0;
            int counter1 = 0;
            while (counter1++ < 10)
            {
                index = random.nextInt(6);
                size = random.nextInt(3) + 1;
                final Tile tile = new Tile(index, size, row.getWidth() / 8, tileHeight);
                try
                {
                    row.addTile(tile);
                }
                catch (final Exception e)
                {

                }
            }
        }
    }

}
