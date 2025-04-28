package management.tool;

import management.table.Item;
import management.table.Orders;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class OrdersTool {

    //配置文件
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;
    //配置文件静态代码块
    static {
        //读取外界的配置文件
        InputStream asStream= ProductTool.class.getResourceAsStream("/account.properties");
        //解析流
        Properties properties=new Properties();
        try {
            //读取流
            properties.load(asStream);
            //获取配置文件中的值
            URL=properties.getProperty("url");
            USER=properties.getProperty("user");
            PASSWORD=properties.getProperty("password");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //合法性判断
    Legitimate legitimate =new Legitimate();
    //调用商品方法
    ProductTool tool1 = new ProductTool();

    private Connection getConnection() throws Exception{
        return DriverManager.getConnection(URL,USER, PASSWORD);
    }

    //查询所有订单id
    public ArrayList<Integer> selectAllOrdersId() throws Exception {
        String sql="select order_id from orders;";
        Connection conn=getConnection();
        ArrayList<Integer> orders = new ArrayList<>();
        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                Integer box = rs.getInt("order_id");
                orders.add(box);
            }
        }catch (Exception e){
            throw e;
        }finally {
            conn.close();
        }
        return orders;
    }
    //添加订单(总价)返回所添加总价的主键
    public void addOrders(Orders orders) throws Exception {
        String sql = "INSERT INTO orders(total_price) VALUES(?);";
        Connection conn = getConnection();
        int anInt;
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            pstmt.setDouble(1, orders.getTotalPrice());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            anInt = rs.getInt(1);
            addOrdersProduct(anInt,orders,conn);
            System.out.println("订单"+anInt+"添加成功");
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }
    //添加每一行订单
    public void addOrdersProduct(int id,Orders orders,Connection conn) throws Exception{
        String sql = "INSERT INTO orders_product(order_id,product_id,quantity) VALUES(?,?,?);";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            List<Item> items = orders.getItems();
            for (Item item : items) {
                    if(!legitimate.checkInt(item.getQuantity())){
                        throw new InvalidDataException("商品"+item.getProductId()+"数量不合法,订单"+id+"添加失败");
                    } else if(checkStockHave(item.getProductId(),item.getQuantity())==-1){//库存不足
                        throw new InvalidDataException("商品"+item.getProductId()+"库存不足,订单"+id+"添加失败");
                    }else{
                        updateProductStock(item.getProductId(),checkStockHave(item.getProductId(),item.getQuantity()));
                    }
                    pstmt.setInt(1, id);
                    pstmt.setInt(2, item.getProductId());
                    pstmt.setInt(3, item.getQuantity());
                    pstmt.addBatch();// 添加批处理
                }
            pstmt.executeBatch(); // 统一执行
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
    }
    //删除订单
    public void deleteOrdersById(int id) throws Exception{
        String sql ="DELETE FROM orders WHERE order_id = ?";
        Connection conn = getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            conn.setAutoCommit(false);
            if(checkOrderById(id)){
                throw new InvalidDataException("订单"+id+"不存在");
            }
            pstmt.setInt(1,id);
            pstmt.executeUpdate();
            System.out.println("订单"+id+"删除成功");
            conn.commit();
        }catch (SQLException e){
            conn.rollback();
            throw e;
        }finally {
            conn.close();
        }
    }
    //删除订单商品表
    public void deleteOrdersProductById(int productId) throws Exception{
        String sql ="DELETE FROM orders_product WHERE product_id = ?";
        Connection conn = getConnection();
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            conn.setAutoCommit(false);
            pstmt.setInt(1,productId);
            pstmt.executeUpdate();
            conn.commit();
        }catch (SQLException e){
            conn.rollback();
            throw e;
        }finally {
            conn.close();
        }
    }
    //检查订单是否存在(订单id)
    public Boolean checkOrderById(int id) throws Exception {
        ArrayList<Integer> product = selectAllOrdersId();
        boolean status = true;
        for (Integer integer : product) {
            if (integer == id) {
                status = false;
                break;
            }
        }
        return status;
    }
    //检查订单商品是否存在
    public Boolean checkOrderProductById(int id,int productId) throws Exception {
        ArrayList<Integer> product = selectAllOrdersProductId(id);
        boolean status = true;
        for (Integer integer : product) {
            if (integer == productId) {
                status = false;
                break;
            }
        }
        return status;
    }
    // 查询订单中的产品ID
    private ArrayList<Integer> selectAllOrdersProductId(int id) throws Exception {
        ArrayList<Integer> product = new ArrayList<>();
        String sql = "select product_id from orders_product where order_id=?";
        Connection conn = getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            conn.setAutoCommit(false);
            pstmt.setInt(1,id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                product.add(rs.getInt("product_id"));
            }
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
        return product;
    }
    //从id得商品信息
    public List<Item> getItemsById(int id) throws Exception {
        String sql="select product_id,quantity from orders_product where order_id=?;";
        Connection conn=getConnection();
        List<Item> items = new ArrayList<>();
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            conn.setAutoCommit(false);
            pstmt.setInt(1,id);
            ResultSet rs =pstmt.executeQuery();
            while (rs.next()){
                Collections.addAll(items,
                        new Item(rs.getInt("product_id"),rs.getInt("quantity"))
                );
            }
            conn.commit();
        }catch (SQLException e){
            conn.rollback();
            throw e;
        }finally {
            conn.close();
        }
        return items;
    }
    //从id得订单
    public Orders getOrderById(int id) throws Exception {
        String sql="select order_id,total_price,order_time from orders where order_id=?;";
        Connection conn=getConnection();
        Orders orders=new Orders();
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            conn.setAutoCommit(false);
            if(checkOrderById(id)){
                throw new InvalidDataException("该订单不存在");
            }
            pstmt.setInt(1,id);
            ResultSet rs =pstmt.executeQuery();
            while (rs.next()){
                orders.setOrderId(rs.getInt("order_id"));
                orders.setTotalPrice(rs.getDouble("total_price"));
                orders.setOrderTime(rs.getDate("order_time"));
                orders.setItems(getItemsById(rs.getInt("order_id")));
            }
            conn.commit();
        }catch (SQLException e){
            conn.rollback();
            throw e;
        }finally {
            conn.close();
        }
        return orders;
    }
    //修改订单中商品个数
    public void updateOrderItem(int id, int productId, int quantity) throws Exception {
        String sql="update orders_product set quantity=? where order_id=? and product_id=?";
        Connection conn=getConnection();
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            conn.setAutoCommit(false);
            if(checkOrderById(id)){
                throw new InvalidDataException("订单"+id+"不存在");
            } else if (checkOrderProductById(id,productId)){
                throw new InvalidDataException("订单"+id+"中不存在"+productId+"号商品");
            } else if (quantity==0) {
                deleteOrdersProductById(productId);
                System.out.println("订单"+id+"商品"+productId+"删除成功");
                return;
            } else if (!legitimate.checkInt(quantity)){
                throw new InvalidDataException("订单"+id+"中修改商品数量不能小于0");
            }else if(checkStockHave(productId,quantity)==-1){//库存不足
                throw new InvalidDataException("商品"+productId+"库存不足,订单"+id+"修改失败");
            }
            updateProductStock(productId,tool1.getProductStock(productId)+checkStockReturn(id,productId,quantity));
            pstmt.setInt(1,quantity);
            pstmt.setInt(2,id);
            pstmt.setInt(3,productId);
            pstmt.executeUpdate();
            System.out.println("订单"+id+"商品"+productId+"数量修改成功");
            conn.commit();
            //事务处理完后实际商品价格更改，保证商品价格更改数据不会出现异常
            updateOrders(id,calculateTotalPrice(getItemsById(id)));
        }catch (Exception e){
            conn.rollback();
            throw e;
        }finally {
            conn.close();
        }
    }
    //修改订单表
    public void updateOrders(int id,double totalPrice) throws Exception {
        String sql="update orders set total_price=? where order_id=?";
        Connection conn=getConnection();
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            conn.setAutoCommit(false);
            pstmt.setDouble(1,totalPrice);
            pstmt.setInt(2,id);
            pstmt.executeUpdate();
            conn.commit();
        }catch (Exception e){
            conn.rollback();
            throw e;
        }finally {
            conn.close();
        }
    }
    //订单排序（下单时间）
    public List<Orders> getAllOrders(String string) throws Exception {
        List<Orders> orders=new ArrayList<>();
        String sql = String.format("SELECT order_id FROM orders ORDER BY %s ASC",string);
        Connection conn = getConnection();

        try(Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)){
            conn.setAutoCommit(false);
            while (rs.next()){
                orders.add(getOrderById(rs.getInt("order_id")));
            }
            conn.commit();
        }catch (SQLException e){
            conn.rollback();
            throw e;
        }finally {
            conn.close();
        }

        return orders;
    }
    //修改库存量
    public void updateProductStock(int id, int stock) throws Exception {
        String sql = "update product set stock=? where product_id=?";
        Connection conn = getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            conn.setAutoCommit(false);
            if (!legitimate.checkInt(stock)) {
                throw new InvalidDataException("商品库存不能低于0");
            }
            pstmt.setInt(1, stock);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }
    //判断订单商品是否有存量
    public int checkStockHave(int id,int stock) throws Exception {
            int i = tool1.getProductStock(id);
            if (i - stock < 0) {
                return -1;
            }
            else {
                return i-stock;
            }
    }
    //判断订单商品是否归还
    public int checkStockReturn(int id,int productId,int quantity) throws Exception {
        List<Item> items = getItemsById(id);
        for (Item item : items) {
            if (item.getProductId() == productId) {
                return item.getQuantity() - quantity;
            }
        }
        return 0;
    }
    //计算总价
    public double calculateTotalPrice(List<Item> items) throws Exception {
        double totalPrice = 0;
        for (Item item : items) {
            totalPrice += tool1.getProductById(item.getProductId()).getProductPrice() * item.getQuantity();
        }
        return totalPrice;
    }
}
