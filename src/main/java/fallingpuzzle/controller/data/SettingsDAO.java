package fallingpuzzle.controller.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SettingsDAO
{

    //Ogni "tabella" del db dovrebbe avere un DAO ( Data Access Object )
    //ed il rispettivo DTO ( Data Transfer Object )
    //Es: SettingsDAO - Setting
    //Il DAO contiene i methodi CRUDS per le query ad db
    //Il DTO � la rappresentazione sotto forma di oggetto
    //di una riga della tabella corrispondente nel db
    //Es: nel mio db ho una tabella che si chiama "Settings"
    //La classe SettingsDAO mi permette di fare le query su questa tabella
    //La classe Setting corrisponde a una riga della tabella

    private static SettingsDAO instance;

    public static List<Setting> getAll()
    {
        initialize();
        final List<Setting> settings = new ArrayList<Setting>();
        final String sql = "select * from Settings";
        try (Statement statement = DatabaseManager.getConnection().createStatement())
        {
            final ResultSet rs = statement.executeQuery(sql);
            while (rs.next())
            {
                final Setting setting = new Setting(rs.getString(0), rs.getString(1));
                settings.add(setting);
            }
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
        }
        return settings;
    }

    public static Setting getById(final String name)
    {
        initialize();
        Setting setting = null;
        final String sql = "select * from Settings where name = ?; ";
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(sql))
        {
            statement.setString(1, name);
            final ResultSet rs = statement.executeQuery();
            if (rs.next())
            {
                setting = new Setting(rs.getString(1), rs.getString(2));
            }
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
        }
        return setting;
    }

    public static void insert(final Setting setting)
    {
        initialize();
        final String sql = "insert or replace into Settings values( ?, ? ); ";
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(sql))
        {
            statement.setString(1, setting.getName());
            statement.setString(2, setting.getValue());
            statement.executeUpdate();
            statement.close();
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static void createTable()
    {
        try
        {
            final Statement statement = DatabaseManager.getConnection().createStatement();
            final String sql = "create table if not exists" + " Settings(" + " name varchar(255) primary key,"
                    + " value varchar(255) ); ";
            statement.executeUpdate(sql);
            statement.close();
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static SettingsDAO initialize()
    {
        if (instance == null)
        {
            instance = new SettingsDAO();
            createTable();
        }
        return instance;
    }

    //SettingsDAO creation methods
    private SettingsDAO()
    {
    }
}
