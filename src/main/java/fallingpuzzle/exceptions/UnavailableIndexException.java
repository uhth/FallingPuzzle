package fallingpuzzle.exceptions;

public class UnavailableIndexException extends TileException
{
    private static final long serialVersionUID = 4556603270051258585L;

    public UnavailableIndexException(final String message)
    {
        super(message);
    }

}
