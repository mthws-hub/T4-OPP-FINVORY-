package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.FinvoryData;
import ec.edu.espe.finvory.view.FrmPrices;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.bson.Document;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

/**
 *
 * @author Arelys Otavalo, the POOwer Rangers of Programming
 */
public class PriceAndTaxController {

    private final FinvoryController mainController;

    public PriceAndTaxController(FinvoryController mainController) {
        this.mainController = mainController;
    }

    public void handleSetPriceAlgorithm() {
        FrmPrices frmPrices = new FrmPrices(mainController);
        frmPrices.setVisible(true);
    }

    public boolean handleUpdatePricesGUI(String profit, String standard, String premium, String vip, String taxInvoice) {
        try {
            BigDecimal profitVal = new BigDecimal(profit);
            BigDecimal standardValue = new BigDecimal(standard);
            BigDecimal premiumValue = new BigDecimal(premium);
            BigDecimal vipValue = new BigDecimal(vip);
            BigDecimal taxValue = new BigDecimal(taxInvoice);

            FinvoryData data = mainController.data;
            data.setProfitPercentage(profitVal);
            data.setDiscountStandard(standardValue);
            data.setDiscountPremium(premiumValue);
            data.setDiscountVip(vipValue);
            data.setTaxRate(taxValue);

            mainController.saveData();
            return true;
        } catch (Exception e) {
            System.err.println("Error actualizando precios: " + e.getMessage());
            return false;
        }
    }

    public boolean handleSetTaxRate(float newRateFloat) {
        try {
            BigDecimal newRate = new BigDecimal(Float.toString(newRateFloat)).setScale(4, RoundingMode.HALF_UP);
            mainController.data.setTaxRate(newRate);
            mainController.saveData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updatePricesWithValidation(String profitStr, String stdStr, String prmStr, String vipStr, String taxStr) {
        String errorMsg = ec.edu.espe.finvory.utils.ValidationUtils.getPriceConfigError(profitStr, stdStr, prmStr, vipStr, taxStr);

        if (errorMsg != null) {
            return false;
        }

        try {
            if (mainController.data != null && mainController.currentCompanyUsername != null) {

                BigDecimal profit = new BigDecimal(profitStr);
                BigDecimal tax = new BigDecimal(taxStr);
                BigDecimal discountStandard = new BigDecimal(stdStr);
                BigDecimal discountPremium = new BigDecimal(prmStr);
                BigDecimal discountVip = new BigDecimal(vipStr);

                mainController.data.setProfitPercentage(profit);
                mainController.data.setTaxRate(tax);
                mainController.data.setDiscountStandard(discountStandard);
                mainController.data.setDiscountPremium(discountPremium);
                mainController.data.setDiscountVip(discountVip);
                mainController.dataBase.saveCompanyData(mainController.data, mainController.currentCompanyUsername);

                syncToMongo(profit, tax, discountStandard, discountPremium, discountVip);

                return true;
            }
        } catch (Exception e) {
            System.err.println("Error en el proceso de guardado: " + e.getMessage());
        }
        return false;
    }

    public void syncPricesToCloud() {
        if (mainController.data == null || mainController.currentCompanyUsername == null) {
            System.err.println("Error: No hay sesión o datos para sincronizar.");
            return;
        }

        try {
            syncToMongo(
                    mainController.data.getProfitPercentage(),
                    mainController.data.getTaxRate(),
                    mainController.data.getDiscountStandard(),
                    mainController.data.getDiscountPremium(),
                    mainController.data.getDiscountVip()
            );
            System.out.println("Sincronización forzada exitosa para: " + mainController.currentCompanyUsername);
        } catch (Exception e) {
            System.err.println("Error en la nube: " + e.getMessage());
        }
    }

    private void syncToMongo(BigDecimal profit, BigDecimal tax, BigDecimal discountStandard, BigDecimal discountPremium, BigDecimal discountVip) {
        MongoDatabase db = ec.edu.espe.finvory.mongo.MongoDBConnection.getDatabaseStatic();
        if (db != null) {
            Document configDoc = new Document()
                    .append("companyUsername", mainController.currentCompanyUsername)
                    .append("taxRate", tax.doubleValue())
                    .append("profitPercentage", profit.doubleValue())
                    .append("discountStandard", discountStandard.doubleValue())
                    .append("discountPremium", discountPremium.doubleValue())
                    .append("discountVip", discountVip.doubleValue());

            db.getCollection("configurations").replaceOne(
                    Filters.eq("companyUsername", mainController.currentCompanyUsername),
                    configDoc,
                    new ReplaceOptions().upsert(true)
            );
        }
    }
}
