package ec.espe.edu.finvory.model;

/**
 *
 * @author Mathews Pastor
 */
public class Report {
    private String selectedPeriod;
    private String reportGenerationDate;
    private int totalSale;
    private String bestSellingProduct;
    private float totalGrossProfit;
    private float totalGrossDay;

    public Report(String selectedPeriod, String reportGenerationDate, int totalSale, String bestSellingProduct, float totalGrossProfit, float totalGrossDay) {
        this.selectedPeriod = selectedPeriod;
        this.reportGenerationDate = reportGenerationDate;
        this.totalSale = totalSale;
        this.bestSellingProduct = bestSellingProduct;
        this.totalGrossProfit = totalGrossProfit;
        this.totalGrossDay = totalGrossDay;
    }

    public String getSelectedPeriod() {
        return selectedPeriod;
    }

    public void setSelectedPeriod(String selectedPeriod) {
        this.selectedPeriod = selectedPeriod;
    }

    public String getReportGenerationDate() {
        return reportGenerationDate;
    }

    public void setReportGenerationDate(String reportGenerationDate) {
        this.reportGenerationDate = reportGenerationDate;
    }

    public int getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(int totalSale) {
        this.totalSale = totalSale;
    }

    public String getBestSellingProduct() {
        return bestSellingProduct;
    }

    public void setBestSellingProduct(String bestSellingProduct) {
        this.bestSellingProduct = bestSellingProduct;
    }

    public float getTotalGrossProfit() {
        return totalGrossProfit;
    }

    public void setTotalGrossProfit(float totalGrossProfit) {
        this.totalGrossProfit = totalGrossProfit;
    }

    public float getTotalGrossDay() {
        return totalGrossDay;
    }

    public void setTotalGrossDay(float totalGrossDay) {
        this.totalGrossDay = totalGrossDay;
    }
    
    
}
