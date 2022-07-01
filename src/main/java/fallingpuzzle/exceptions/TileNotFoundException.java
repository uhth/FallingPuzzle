package fallingpuzzle.exceptions;

public class TileNotFoundException extends TileException
{
    private static final long serialVersionUID = 5543564279468345932L;

    public TileNotFoundException(final String message)
    {
        super(message);
    }

}
