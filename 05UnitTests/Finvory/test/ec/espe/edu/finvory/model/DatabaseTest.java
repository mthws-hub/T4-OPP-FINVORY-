package ec.espe.edu.finvory.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileLock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Joseph Medina
 */
public class DatabaseTest {
    
    private Database instance;
    private FinvoryData testData;
    private static final String DATA_FOLDER = "data";
    private static final String CLIENTS_FILE = DATA_FOLDER + File.separator + "clients.csv";
    private static final String SUPPLIERS_FILE = DATA_FOLDER + File.separator + "suppliers.csv";
    private static final String JSON_FILE = "utils" + File.separator + "finvory_database.json";

    public DatabaseTest() {
    }
    
    @Before
    public void setUp() {
        instance = new Database();
        testData = new FinvoryData();
    }
    
    @After
    public void tearDown() {
        new File(CLIENTS_FILE).delete();
        new File(SUPPLIERS_FILE).delete();
        new File(JSON_FILE).delete();
        new File(DATA_FOLDER).delete();
    }

    @Test
    public void testDatabaseInitialization() {
        assertNotNull(instance);
    }

    @Test
    public void testJsonFileCreation() {
        instance.save(testData);
        File f = new File(JSON_FILE);
        assertTrue(f.exists());
    }

    @Test
    public void testLoadEmptyDatabase() {
        new File(JSON_FILE).delete();
        FinvoryData loaded = instance.load();
        assertNotNull(loaded);
    }

    @Test
    public void testSemicolonEscaping() {
        Customer c = new Customer("Company; Broken", "17001", "099", "m", "V");
        testData.getCustomers().add(c);
        instance.save(testData);
        FinvoryData loaded = instance.load();
        assertEquals("Company; Broken", loaded.getCustomers().get(0).getName());
    }

    @Test
    public void testNewlineEscaping() {
        Supplier s = new Supplier("Name", "1", "1", "m", "Line1\nLine2");
        testData.getSuppliers().add(s);
        instance.save(testData);
        FinvoryData loaded = instance.load();
        assertEquals(1, loaded.getSuppliers().size());
    }

    @Test(expected = IOException.class)
    public void testSavePropagatesIoException() throws IOException {
        instance.save(testData);
        File f = new File(CLIENTS_FILE);
        f.setReadOnly();
        instance.save(testData);
    }

    @Test
    public void testDistinguishNullFromLiteralStringNull() {
        Supplier s = new Supplier("N", "1", "1", "m", null);
        testData.getSuppliers().add(s);
        instance.save(testData);
        FinvoryData loaded = instance.load();
        assertNull(loaded.getSuppliers().get(0).getDescription());
    }

    @Test(expected = RuntimeException.class)
    public void testLoadCorruptedLineThrowsException() throws IOException {
        new File(DATA_FOLDER).mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(CLIENTS_FILE))) {
            pw.println("Identification;FullName;Phone;Email;ClientType");
            pw.println("Incomplete;Line;Data");
        }
        instance.load();
    }

    @Test
    public void testStrictHeaderValidation() throws IOException {
        new File(DATA_FOLDER).mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(CLIENTS_FILE))) {
            pw.println("HACKED_HEADER;DATA;DATA");
            pw.println("1;2;3;4;5");
        }
        FinvoryData loaded = instance.load();
        assertTrue(loaded.getCustomers().isEmpty());
    }

    @Test
    public void testAtomicWriteIntegrity() {
        testData.getCustomers().add(new Customer("A", "1", "1", "1", "1"));
        try {
            instance.save(null);
        } catch (Exception e) {}
        
        File f = new File(CLIENTS_FILE);
        assertFalse(f.exists());
    }

    @Test(expected = IOException.class)
    public void testDirectoryPermissionErrorPropagation() throws IOException {
        File folder = new File(DATA_FOLDER);
        folder.mkdirs();
        folder.setWritable(false);
        try {
            instance.save(testData);
        } finally {
            folder.setWritable(true);
        }
    }

    @Test
    public void testTrimWhitespaceOnLoad() {
        Customer c = new Customer("  Space  ", "1", "1", "m", "V");
        testData.getCustomers().add(c);
        instance.save(testData);
        FinvoryData loaded = instance.load();
        assertEquals("Space", loaded.getCustomers().get(0).getName());
    }

    @Test(expected = IOException.class)
    public void testFileLockHandling() throws IOException {
        new File(DATA_FOLDER).mkdirs();
        try (FileOutputStream fos = new FileOutputStream(CLIENTS_FILE);
             FileLock lock = fos.getChannel().lock()) {
            instance.save(testData);
        }
    }

    @Test
    public void testRejectDuplicateIdsOnLoad() throws IOException {
        new File(DATA_FOLDER).mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(CLIENTS_FILE))) {
            pw.println("Header");
            pw.println("17001;User1;0;m;V");
            pw.println("17001;User1Duplicate;0;m;V");
        }
        FinvoryData loaded = instance.load();
        assertEquals(1, loaded.getCustomers().size());
    }

    @Test
    public void testQuoteInjectionHandling() {
        Customer c = new Customer("Alias \"Boss\"", "1", "1", "m", "V");
        testData.getCustomers().add(c);
        instance.save(testData);
        FinvoryData loaded = instance.load();
        assertEquals("Alias \"Boss\"", loaded.getCustomers().get(0).getName());
    }
}