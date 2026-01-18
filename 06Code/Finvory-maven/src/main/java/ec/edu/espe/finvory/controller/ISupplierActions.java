package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.Supplier;
import java.util.List;

/**
 *
 * @author Maryuri Quiña, @ESPE
 */
public interface ISupplierActions {

    List<Object[]> getSupplierTableData();

    Supplier findSupplierByRuc(String ruc);

    boolean deleteSupplierByRuc(String ruc);
}
