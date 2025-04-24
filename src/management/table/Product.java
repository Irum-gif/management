package management.table;
//商品
public class Product {
    // 商品编号
    private Integer productId;
    // 商品名
    private String productName;
    // 商品价格
    private Double productPrice;
    // 库存数量
    private Integer stock;
    public Product(){

    }

    public Product(String productName, Double productPrice, Integer stock) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.stock = stock;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", stock=" + stock +
                '}';
    }
}
