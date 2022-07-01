package fallingpuzzle.exceptions;

public class DuplicateTileException extends TileException
{
    private static final long serialVersionUID = -95231704498795918L;

    public DuplicateTileException(final String message)
    {
        super(message);
    }
}
