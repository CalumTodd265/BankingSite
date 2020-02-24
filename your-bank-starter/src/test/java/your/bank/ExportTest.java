package your.bank;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ExportTest {

    @Test
    public void exportCreation(){
        Account a = new Account("yup",new BigDecimal(5.00),"GBP");
        Account z = new Account("Craik", new BigDecimal(20.00), "USD");
        ArrayList<Account> b = new ArrayList<>();
        b.add(a);
        b.add(z);
        Export exp = new Export(b);
        exp.exportList();
        assertNotNull(exp);
    }

    @Test
    public void stringReturnTest(){
        Account a = new Account("yup",new BigDecimal(5.00),"GBP");
        ArrayList<Account> b = new ArrayList<>();
        b.add(a);
        Export exp = new Export(b);
        assertEquals(exp.exportList(), "name,inital balance,current balance,currency,transactions processed,transactions failed\nyup,5.00,5.00,GBP,0,0\n");
    }

    @Test
    public void inlineCommaTest(){
        Account a = new Account("y,up",new BigDecimal(5.00),"GBP");
        ArrayList<Account> b = new ArrayList<>();
        b.add(a);
        Export exp = new Export(b);
        assertEquals(exp.exportList(), "name,inital balance,current balance,currency,transactions processed,transactions failed\nyup,5.00,5.00,GBP,0,0\n");
    }

    @Test
    public void multipleInlineCommaTest(){
        Account a = new Account("y,u,p",new BigDecimal(5.00),"GBP");
        ArrayList<Account> b = new ArrayList<>();
        b.add(a);
        Export exp = new Export(b);
        assertEquals(exp.exportList(), "name,inital balance,current balance,currency,transactions processed,transactions failed\nyup,5.00,5.00,GBP,0,0\n");
    }

    @Test
    public void multipleInlineCommmaTestMulElements(){
        Account a = new Account("y,u,p",new BigDecimal(5.00),"GBP");
        Account c = new Account("Joh,n", new BigDecimal(10.00), "USD");
        ArrayList<Account> b = new ArrayList<>();
        b.add(a);
        b.add(c);
        Export exp = new Export(b);
        assertEquals(exp.exportList(), "name,inital balance,current balance,currency,transactions processed,transactions failed\nyup,5.00,5.00,GBP,0,0\nJohn,10.00,10.00,USD,0,0\n");
    }

    @Test
    public void stringToFile() throws java.io.IOException{
        Account a = new Account("y,u,p",new BigDecimal(5.00),"GBP");
        Account c = new Account("Joh,n", new BigDecimal(10.00), "USD");
        ArrayList<Account> b = new ArrayList<>();
        b.add(a);
        b.add(c);
        Export exp = new Export(b);
        assertNotNull(exp.getOut());
    }
}
