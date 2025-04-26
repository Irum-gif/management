package management;

import management.table.Item;
import management.table.Product;
import management.table.Orders;
import management.tool.Producttest;
import management.tool.Orderstest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Testcases {
    Producttest tool1 = new Producttest();
    Orderstest tool2= new Orderstest();
    @Test
    //添加商品
    public void method1() {
        try {
            List<Product> products = new ArrayList<>();
            Collections.addAll(products,
                    new Product("苹果", 2.0, 5),
                    new Product("香蕉", 3.0, 10),
                    new Product("橙子", 4.0, 15),
                    new Product("西瓜", 5.0, 20),
                    new Product("葡萄", 6.0, 25),
                    new Product("草莓", 7.0, 30),
                    new Product("樱桃", 8.0, 35),
                    new Product("芒果", 9.0, 40),
                    new Product("菠萝", 10.0, 45),
                    new Product("梨", 11.0, 50),
                    new Product("柚子", 12.0, 55),
                    new Product("荔枝", 13.0, 60)
            );
            for (Product product : products) {
                tool1.addProduct(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    //删除商品
    public void method2() {
        try {
            tool1.deleteProductById(12);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    //修改商品
    public void method3() {
        try {
            tool1.updateProduct("草莓", 2, 50,3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    //查询商品
    public void method4() {
        try {
            System.out.println(tool1.getProductById(3));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    //商品排序
    public void method5() throws Exception {
        List<Product> products = tool1.productSort();
        for (Product product : products) {
            System.out.println(product);
        }
    }
    @Test
    //添加订单
    public void method6() {
        try {
            List<Orders> orders= new ArrayList<>();
            List<Item> items1=new ArrayList<>();
            List<Item> items2=new ArrayList<>();
            Collections.addAll(items1,
                    new Item(1,1),
                    new Item(2,1),
                    new Item(3,3)
            );
            Collections.addAll(items2,
                    new Item(2,2),
                    new Item(3,1),
                    new Item(4,2)
            );
            Collections.addAll(orders,
                    new Orders(tool2.calculateTotalPrice(items1), items1),
                    new Orders(tool2.calculateTotalPrice(items2), items2)
            );
            for (Orders order : orders) {
                tool2.addOrders(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    //删除订单
    public void method7() {
        try {
            tool2.deleteOrdersById(6);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    //修改订单
    public void method8() {
        try {
            tool2.updateOrderItem(2,6,10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    //查询订单
    public void method9() {
        try {
            System.out.println(tool2.getOrderById(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    //排序所有订单
    public void method10() {
        try {
            List<Orders> orders=tool2.getAllOrders("total_price");
            for (Orders order : orders) {
                System.out.println(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    //小测试
    public void method11() throws Exception {
        System.out.println(tool2.getItemsById(2));
    }
}
