package del4;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database{

	private Connection connection; 
	
	public Database(String uri, String username, String password, String driver_cname) {
		this.connection = establishConnection(uri, username, password, driver_cname); 
	}
	
	// geters and setters 
	public Connection getConnection(){
		return this.connection; 
	}
   
    // queries
	
	final static String SELECT_OWNERS = "SELECT*\n"
			+ "FROM OWNER;";
	final static String SELECT_SHIRTS_BY_OWNER_BRAND = "SELECT ITEM.SlotNumber, ITEM.ShelfNumber, OFirstName, OMiddleName, OLastName, Brand.BrandName, Type, CLOTHING.Material\n"
		
			+ "FROM OWNER \n"
			+ "JOIN OWNS ON OWNER.OID = OWNS.OID\n"
			+ "JOIN ITEM ON ITEM.SlotNumber = OWNS.SlotNumber AND ITEM.ShelfNumber = OWNS.ShelfNumber\n"
			+ "JOIN CLOTHING ON CLOTHING.ClothingID = ITEM.ClothingID\n"
			+ "JOIN BRAND ON BRAND.BrandName = CLOTHING.BrandName\n"
			+ "JOIN SHIRT ON SHIRT.ClothingID = CLOTHING.ClothingID;";
	
	    
    final public static String SELECT_ALL_SHIRTS ="SELECT OFirstName, OMiddleName, OLastName, Brand.BrandName, Type, CLOTHING.Material\n"
    		+ "FROM OWNER \n"
    		+ "JOIN OWNS ON OWNER.OID = OWNS.OID\n"
    		+ "JOIN ITEM ON ITEM.SlotNumber = OWNS.SlotNumber AND ITEM.ShelfNumber = OWNS.ShelfNumber\n"
    		+ "JOIN CLOTHING ON CLOTHING.ClothingID = ITEM.ClothingID\n"
    		+ "JOIN BRAND ON BRAND.BrandName = CLOTHING.BrandName\n"
    		+ "JOIN SHIRT ON SHIRT.ClothingID = CLOTHING.ClothingID;";
    
    final static String SELECT_ALL_PANTS = "SELECT OFirstName, OMiddleName, OLastName, Brand.BrandName, isLong, CLOTHING.Material\n"
    		+ "FROM OWNER\n" 
    		+ "JOIN OWNS ON OWNER.OID = OWNS.OID\n"
    		+ "JOIN ITEM ON ITEM.SlotNumber = OWNS.SlotNumber AND ITEM.ShelfNumber = OWNS.ShelfNumber\n"
    		+ "JOIN CLOTHING ON CLOTHING.ClothingID = ITEM.ClothingID\n"
    		+ "JOIN BRAND ON BRAND.BrandName = CLOTHING.BrandName\n"
    		+ "JOIN PANTS ON PANTS.ClothingID = CLOTHING.ClothingID;";
    
    final static String SELECT_ALL_OUTERWEAR ="SELECT OFirstName, OMiddleName, OLastName, Brand.BrandName, isJacket, CLOTHING.Material\r\n"
    		+ "FROM OWNER \r\n"
    		+ "JOIN OWNS ON OWNER.OID = OWNS.OID\r\n"
    		+ "JOIN ITEM ON ITEM.SlotNumber = OWNS.SlotNumber AND ITEM.ShelfNumber = OWNS.ShelfNumber\r\n"
    		+ "JOIN CLOTHING ON CLOTHING.ClothingID = ITEM.ClothingID\r\n"
    		+ "JOIN BRAND ON BRAND.BrandName = CLOTHING.BrandName\r\n"
    		+ "JOIN OUTERWEAR ON OUTERWEAR.ClothingID = CLOTHING.ClothingID;";
    
    final static String SELECT_ALL_CLOTHING_BY_WORN = "SELECT OFirstName, OMiddleName, OLastName, CLOTHING.ClothingID, DateWorn\r\n"
    		+ "FROM OWNER \r\n"
    		+ "JOIN OWNS ON OWNS.OID = OWNER.OID\r\n"
    		+ "JOIN ITEM ON ITEM.SlotNumber = OWNS.SlotNumber AND ITEM.ShelfNumber = OWNS.ShelfNumber\r\n"
    		+ "JOIN CLOTHING ON CLOTHING.ClothingID = ITEM.ClothingID;";
    
    final static String SELECT_ITEM_WHERE_LOCATION = "SELECT OFirstName, OMiddleName, OLastName, CLOTHING.ClothingID, DateWorn\r\n"
    		+ "FROM OWNER \r\n"
    		+ "JOIN OWNS ON OWNS.OID = OWNER.OID\r\n"
    		+ "JOIN ITEM ON ITEM.SlotNumber = OWNS.SlotNumber AND ITEM.ShelfNumber = OWNS.ShelfNumber\r\n"
    		+ "JOIN CLOTHING ON CLOTHING.ClothingID = ITEM.ClothingID\r\n"
    		+ "WHERE ITEM.ShelfNumber = ? AND ITEM.SlotNumber = ?;";
    
    final static String SELECT_ALL_CLOTHING_OWNED = "SELECT OFirstName, OMiddleName, OLastName, S.ClothingID, ITEM.ShelfNumber, ITEM.SlotNumber\n"
    		+ "FROM\n"
    		+ "(( \n"
    		+ "SELECT CLOTHING.ClothingID\n"
    		+ "FROM CLOTHING\n"
    		+ " JOIN SHIRT ON SHIRT.ClothingID = CLOTHING.ClothingID)\n"
    		+ " UNION \n"
    		+ " (\n"
    		+ " SELECT CLOTHING.ClothingID\n"
    		+ "FROM CLOTHING\n"
    		+ " JOIN PANTS ON PANTS.ClothingID = CLOTHING.ClothingID\n"
    		+ " )\n"
    		+ " UNION\n"
    		+ "  (\n"
    		+ " SELECT CLOTHING.ClothingID\n"
    		+ "FROM CLOTHING\n"
    		+ " JOIN OUTERWEAR ON OUTERWEAR.ClothingID = CLOTHING.ClothingID\n"
    		+ " ) ) AS S\n"
    		+ " \r\n"
    		+ " JOIN ITEM ON S.ClothingID = ITEM.ClothingID\n"
    		+ " JOIN OWNS ON OWNS.SlotNumber = ITEM.SlotNumber AND OWNS.ShelfNumber = ITEM.ShelfNumber\n"
    		+ " JOIN OWNER ON OWNS.OID = OWNER.OID;";
    
    final static String SELECT_ALL_CLOTHING_NOT_OWNED = "SELECT Clothing.ClothingID, BRAND.BrandName, I.ShelfNumber, I.SlotNumber\n"
    		+ " FROM ITEM AS I\n"
    		+ " JOIN CLOTHING ON I.ClothingID = CLOTHING.ClothingID\n"
    		+ " JOIN BRAND ON BRAND.BrandName = CLOTHING.BrandName\n"
    		+ " WHERE NOT EXISTS \n"
    		+ "(\n"
    		+ "	SELECT * \n"
    		+ "    FROM OWNS \r\n"
    		+ "    WHERE I.SlotNumber = OWNS.SlotNumber \n"
    		+ "		AND I.ShelfNumber = OWNS.ShelfNumber\n"
    		+ ");";
    
    final static String SELECT_BRAND_WHERE_CLOTHINGID = "SELECT B.BrandName, year FROM CLOTHING JOIN BRAND B ON CLOTHING.BrandName = B.BrandName\n"
    		+ "WHERE CLOTHINGID = ?"; 
    
    final static String SELECT_CLOTHING_WHERE_CLOTHINGID = "SELECT ClothingId, Material, BrandName\n"
    		+ "FROM CLOTHING \n"
    		+ "WHERE ClothingId = ?; "; 
    
    final static String SELECT_ALL_CLOTHING_BY_OWNED = "SELECT OFirstName, OMiddleName, OLastName, S.ClothingID, ITEM.ShelfNumber, ITEM.SlotNumber\n"
    		+ "FROM\n"
    		+ "(( \n"
    		+ "SELECT CLOTHING.ClothingID\n"
    		+ "FROM CLOTHING\n"
    		+ " JOIN SHIRT ON SHIRT.ClothingID = CLOTHING.ClothingID)\n"
    		+ " UNION \n"
    		+ " (\n"
    		+ " SELECT CLOTHING.ClothingID\n"
    		+ "FROM CLOTHING\n"
    		+ " JOIN PANTS ON PANTS.ClothingID = CLOTHING.ClothingID\n"
    		+ " )\n"
    		+ " UNION\n"
    		+ "  (\n"
    		+ " SELECT CLOTHING.ClothingID\n"
    		+ "FROM CLOTHING\n"
    		+ " JOIN OUTERWEAR ON OUTERWEAR.ClothingID = CLOTHING.ClothingID\n"
    		+ " ) ) AS S\n"
    		+ " \r\n"
    		+ " JOIN ITEM ON S.ClothingID = ITEM.ClothingID\n"
    		+ " JOIN OWNS ON OWNS.SlotNumber = ITEM.SlotNumber AND OWNS.ShelfNumber = ITEM.ShelfNumber\n"
    		+ " JOIN OWNER ON OWNS.OID = OWNER.OID;"
    		+ "	WHERE OWNS.OID =?";
    
    final static String INSERT_OWNER = "INSERT INTO OWNER \n"
    		+ "VALUES (?, \"?\", \"?\", \"?\",\"?\");";
    
    final static String INSERT_BRAND = "INSERT INTO BRAND\r\n"
    		+ "VALUES(\"?\", ?);";
    
    final static String INSERT_CLOTHING = "INSERT INTO CLOTHING\n"
    		+ "VALUES(?, \"?\", \"?\");";
    
    final static String INSERT_ITEM = "INSERT INTO ITEM\n"
    		+ "VALUES(\"?\", ?, ?, ?, ?, ?);";
    
    //need to make method for
    final static String INSERT_COLOR = "INSERT INTO COLOR\n"
    		+ "VALUES(\"?\");";
    
    final static String INSERT_PANTS = "INSERT INTO PANTS\n"
    		+ "VALUES(?, ?);";
    
    final static String INSERT_SHIRT = "INSERT INTO SHIRT\n"
    		+ "VAlUES (?, \"?\");";
    
    final static String INSERT_OUTERWEAR = "INSERT INTO OUTERWEAR\n"
    		+ "VALUES(?, ?);";
    
    final static String INSERT_OWNS = "INSERT INTO OWNS\n"
    		+ "VALUES(?, '?', '?', ?, 3?;";
    
    final static String INSERT_HAS_COLOR = "INSERT INTO HAS_COLOR\n"
    		+ "VAlUES(\"?\", ?, ?);";
    
    final static String DELETE_OWNER = "DELETE FROM OWNER\n"
    		+ "WHERE ?= OID AND \"?\"=OFirstName AND \"?\"=OMiddleName AND \"?\"=OMiddleName AND \"?\" = OLastName AND \"?\"=Size;";
    
    final static String DELETE_OWNS = "DELETE FROM OWNS\n"
    		+ "WHERE ?=OID AND '?'=DateWorn AND '?'=DateAquired AND ?=ShelfNumber AND ?=SlotNumber;";
    
    final static String DELETE_CLOTHING = "DELETE FROM CLOTHING\n"
    		+ "WHERE ?=ClothingID AND \"?\"=Material AND \"?\"=BrandName;";
    
    final static String DELETE_COLOR = "DELETE FROM COLOR\n"
    		+ "WHERE \"?\"=ColorName;";
    
    final static String DELETE_HAS_COLOR = "DELETE FROM COLOR\n"
    		+ "WHERE \"?\"=ColorName AND ?=ShelfNumber AND ?=SlotNumber;";
    
    final static String DELETE_ITEM = "DELETE FROM ITEM\n"
    		+ "WHERE \"?\"=Size AND ? = isClean AND ?=isDamaged AND ?=ShelfNumber AND ?=SlotNumber AND ?=ClothingID;";
    
    final static String DELETE_OUTERWEAR = "DELETE FROM OUTERWEAR\n"
    		+ "WHERE ?=ClothingID AND ?=isJacket;";
    
    final static String DELETE_SHIRT = "DELETE FROM SHIRT\n"
    		+ "WHERE ?=ClothingID AND \"?\"=Type;";
    
    final static String DELETE_PANTS = "DELETE FROM OUTERPANTS\n"
    		+ "WHERE ?=ClothingID AND ?=isLong;";
    
    /**
     * runs a query based on in file constant
     * @param query
     * @return
     * @throws SQLException
     */
    public PreparedStatement runQuery(String query, ArrayList<Object> args) throws SQLException{
    	
    	PreparedStatement s = null;  
	
		s = connection.prepareStatement(query); 
		
		if(args!=null)
			setPreparedStatementArgs(args, s); 

    	return s; 

    }
    
   
    private void setPreparedStatementArgs(ArrayList<Object> args, PreparedStatement s) throws SQLException {
		for(int i = 0; i < args.size(); i++)
			s.setString(i+1, String.valueOf(args.get(i))); // Indexes starting @ 1 not 0 
    }
 
   
	/**
	 * establishConnection 
	 * @desc opens the connection to the database
	 * @param uri - uri to database 
	 * @param username - user name for database 
	 * @param password - password for database
	 * @param driver_cname - database driver class name 
	 * @return reference pointer to connection object 
	 */
    private Connection establishConnection(String uri, String username, String password, String driver_cname){
            Connection connection  = null; 
            try {
                // Load driver 
                Class.forName(driver_cname); 
                
                // get connection
                connection = DriverManager.getConnection(uri, username, password);

            }catch(SQLException e) {
                e.printStackTrace();
            }catch (ClassNotFoundException e) {
                System.out.println("class not found "+driver_cname);
                System.exit(1);
            }
            
            return connection;
    }

    public void closeConnection() {
    	
    	try {
        	this.connection.close();
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}

    }
        
}
