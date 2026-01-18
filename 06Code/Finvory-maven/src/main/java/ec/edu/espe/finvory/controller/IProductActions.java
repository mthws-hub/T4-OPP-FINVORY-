package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.Product;
import ec.edu.espe.finvory.model.Inventory;
import java.util.List;

/**
 *
 * @author Maryuri Quiña, @ESPE
 */
public interface IProductActions {

    List<Object[]> getProductTableData(Inventory specificInventory);

    Product findProductById(String productId);

    boolean deleteProductById(String productId);

    void loadProductTable();
}
