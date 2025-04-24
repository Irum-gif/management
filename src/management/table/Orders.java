package management.table;

import java.util.Date;
import java.util.List;

public class Orders {
    //订单编号
    private Integer orderId;
    //总价
    private Double totalPrice;
    //下单时间
    private Date orderTime;
    //关联商品
    private List<Item> items;
    public Orders() {
    }

    public Orders( Double totalPrice, List<Item> items) {
        this.totalPrice = totalPrice;
        this.items = items;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "orderId=" + orderId +
                ", totalPrice=" + totalPrice +
                ", orderTime=" + orderTime +
                ", items=" + items +
                '}';
    }
}
