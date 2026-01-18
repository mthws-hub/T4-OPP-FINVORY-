package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.Customer;
import java.util.List;

/**
 *
 * @author Maryuri Quiña, @ESPE
 */
public interface ICustomerActions {

    void loadCustomerTable();

    Customer findCustomerById(String identification);

    boolean deleteCustomerById(String customerId);

    boolean createNewCustomer(Customer customer);

    List<Object[]> getCustomerTableData();

}
