package ir.amingoli.mycoustomer.model;

public class GetTotalSales {
    Double totalSales;
    int salesCount;

    public GetTotalSales() {
    }

    public GetTotalSales(Double totalSales, int salesCount) {
        this.totalSales = totalSales;
        this.salesCount = salesCount;
    }

    public Double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Double totalSales) {
        this.totalSales = totalSales;
    }

    public int getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(int salesCount) {
        this.salesCount = salesCount;
    }
}
