package management.table;

public class OrdersProduct {
    //订单编号
    private Integer  orderId;
    //商品信息
    private Integer  productId;
    //下单数量
    private Integer quantity;

    public OrdersProduct(){}

    public OrdersProduct(Integer orderId, Integer productId, Integer quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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
        return "Orders{" +
                "orderId=" + orderId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
