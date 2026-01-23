package ec.edu.espe.finvory.model;

import java.math.BigDecimal;
import ec.edu.espe.finvory.controller.*;

/**
 *
 * @author Arelys Otavalo, the POOwer Rangers of Programming
 */
public class TaxManager {

    private static TaxManager instance;
    private BigDecimal taxRate;

    private TaxManager() {
        this.taxRate = new BigDecimal("0.15");
    }

    public static synchronized TaxManager getInstance() {
        if (instance == null) {
            instance = new TaxManager();
        }
        return instance;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

}
