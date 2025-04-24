package management.table;

public class Item {
    private Integer productId;
    private Integer quantity;

    public Item() {
    }

    public Item(Integer productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "item{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
