package fallingpuzzle.controller.ia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import fallingpuzzle.controller.scene.GameController;
import fallingpuzzle.model.Row;
import fallingpuzzle.model.Tile;
import fallingpuzzle.model.TileMove;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DLVController
{

    GameController gameController;

    Process dlvProcess;

    BufferedReader outReaderFromProc;

    BufferedReader errReaderFromProc;

    File dlvExe;

    public DLVController(final GameController gameController)
    {
        this.gameController = gameController;
        try
        {
            lookForDLV2();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    public TileMove getTileMoveFromOutput()
    {
        TileMove tileMove = null;
        try
        {
            errReaderFromProc.lines().forEach(error -> log.error("{}", error));
            tileMove = processOutput(outReaderFromProc.lines());
            errReaderFromProc.close();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        return tileMove;

    }

    public Process startProcess()
    {
        final Runtime rt = Runtime.getRuntime();
        // "--no-facts"
        final String[] commands = { dlvExe.getAbsolutePath(), "--stdin", "--no-facts" };
        dlvProcess = null;
        try
        {
            dlvProcess = rt.exec(commands);
            outReaderFromProc = new BufferedReader(new InputStreamReader(dlvProcess.getInputStream(), Charset.defaultCharset()));
            errReaderFromProc = new BufferedReader(new InputStreamReader(dlvProcess.getErrorStream(), Charset.defaultCharset()));
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        return dlvProcess;
    }

    private void lookForDLV2() throws FileNotFoundException
    {
        final File dlv2 = new File(getClass().getResource("/dlv").getPath());
        final File[] files = dlv2.listFiles((pathname) ->
            {
                final Pattern p = Pattern.compile("\\..*", Pattern.CASE_INSENSITIVE);
                final Matcher m = p.matcher(pathname.getName());
                return !m.matches();
            });
        if (files == null || files.length == 0)
        {
            log.error("{}", "PUT DLV*.EXE INSIDE /resources/dlv");
            throw new FileNotFoundException("MISSING DLV2 EXECUTABLE");
        }
        dlvExe = files[0];
    }

    private TileMove processOutput(final Stream<String> output) throws IOException
    {
        final HashMap<String, Integer> info = new HashMap<>();

        output.forEach(line ->
            {
                final Pattern pattern = Pattern
                        .compile("(.*)(tileMove\\((?<firstIndex>[0-9]+),(?<newIndex>[0-9]+),(?<rowIndex>[0-9]+)\\))(.*)");
                final Matcher matcher = pattern.matcher(line);
                log.info("{}", line);
                while (matcher.find())
                {
                    info.put("firstIndex", Integer.parseInt(matcher.group("firstIndex")));
                    info.put("newIndex", Integer.parseInt(matcher.group("newIndex")));
                    info.put("rowIndex", Integer.parseInt(matcher.group("rowIndex")));
                }
            });

        if (info.entrySet().isEmpty())
        {
            return null;
        }

        final Row row = gameController.getRow(info.get("rowIndex"));
        final Tile tile = row.getTile(info.get("firstIndex"));
        return new TileMove(tile, info.get("newIndex"));
    }

}
