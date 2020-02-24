package your.bank;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.List;
/***
 * Class used to export the list of accounts to a csv file
 */
public class Export {
    public List<Account> accounts;
    public Export(List<Account> aclist){
        accounts = aclist;
    }


    /***
     * method to convert the contents of a list into a string
     * @return the contents of the list as a comma seperated string
     */
    public String exportList(){
        String csvOutput = "";

        csvOutput += "name,inital balance,current balance,currency,transactions processed,transactions failed\n";

        for(int i = 0; i < accounts.size();i++){
            String temp = accounts.get(i).getName().replaceAll(",","");
            csvOutput += temp + ",";
            temp = accounts.get(i).getInitialbalance().toString().replaceAll(",", "");
            csvOutput += temp + ",";
            temp = accounts.get(i).getCurrentBalance().toString().replaceAll(",", "");
            csvOutput += temp + ",";
            temp = accounts.get(i).getCurrency().replaceAll(",", "");
            csvOutput += temp + ",";
            temp = Integer.toString(accounts.get(i).getTransactionsProcessed());
            csvOutput += temp + ",";
            temp = Integer.toString(accounts.get(i).getTransactionsFailed());
            csvOutput += temp + "\n";
        }

        return csvOutput;
    }

    /***
     * method to package a string
     * @return a file containing a comma seperated string
     */
    public File getOut() throws java.io.IOException {
        File out = new File("out.csv");
        FileUtils.writeStringToFile(out, exportList());

        return out;

    }
}


