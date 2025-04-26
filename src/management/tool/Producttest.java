package management.tool;

import management.table.Product;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class Producttest {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;
    //配置文件静态代码块
    static {
        //读取外界的配置文件
        InputStream asStream=Producttest.class.getResourceAsStream("/account.properties");
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

    String insertColumn= String.format("%s,%s,%s","product_name","product_price","stock");

    //获取数据库连接
    private Connection getConnection() throws Exception{
        return DriverManager.getConnection(URL,USER, PASSWORD);
    }

    //合法性判断
    Legimitate legimitate=new Legimitate();

    //查询所有商品名
    public ArrayList<String> selectAllProductName() throws Exception {
        //定义sql语句
        String sql="select product_name from product;";
        //获取链接
        Connection conn=getConnection();
        ArrayList<String> products = new ArrayList<>();
        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                String str = rs.getString("product_name");
                products.add(str);
            }
        }catch (Exception e){
            throw e;
        }finally {
            conn.close();
        }
        return products;
    }
    //查询所有商品id
    public ArrayList<Integer> selectAllProductId() throws Exception {
        //定义sql语句
        String sql="select product_id from product;";
        //获取链接
        Connection conn=getConnection();
        ArrayList<Integer> products = new ArrayList<>();
        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                Integer box = rs.getInt("product_id");
                products.add(box);
            }
        }catch (Exception e){
            throw e;
        }finally {
            conn.close();
        }
        return products;
    }
    //查询所有订单中的商品id
    public ArrayList<Integer> selectAllOrderId() throws Exception {
        //定义sql语句
        String sql = "select product_id from orders_product";
        //获取链接
        Connection conn = getConnection();
        ArrayList<Integer> products = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                products.add(rs.getInt("product_id"));
            }
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
        return products;
    }
    //添加商品
    public void addProduct(Product product) throws Exception {
        //定义sql语句
        String sql=String.format("insert into product(%s) values(?,?,?);",insertColumn);
        //获取链接
        Connection conn=getConnection();
        //获取pstmt对象
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try{
            conn.setAutoCommit(false);
            if(!legimitate.checkDouble(product.getProductPrice())){
                throw new InvalidDataException("商品"+product.getProductName()+"价格不能低于0");
            } else if (!legimitate.checkInt(product.getStock())){
                throw new InvalidDataException("商品"+product.getProductName()+"库存数量不能低于0");
            } else if (!checkProductByName(product.getProductName())) {
                throw new InvalidDataException("商品"+product.getProductName()+"已存在");
            }
            pstmt.setString(1, product.getProductName());
            pstmt.setDouble(2, product.getProductPrice());
            pstmt.setInt(3,product.getStock());
            pstmt.executeUpdate();
            System.out.println("商品"+product.getProductName()+"添加成功");
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }finally {
            pstmt.close();
            conn.close();
        }
    }
    //修改商品
    public void updateProduct(String name ,double price,int stock, int id) throws Exception {
        //定义sql语句
        String sql="update product set product_name=?,product_price=?,stock=? where product_id=?";
        //获取链接
        Connection conn=getConnection();
        //获取pstmt对象
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try{
            conn.setAutoCommit(false);
            if(checkProductById(id)){
                throw new InvalidDataException("该商品不存在");
            } else if(!legimitate.checkDouble(price)){
                throw new InvalidDataException("商品价格不能低于0");
            } else if (!legimitate.checkInt(stock)){
                throw new InvalidDataException("商品库存不能低于0");
            }
            pstmt.setString(1,name);
            pstmt.setDouble(2,price);
            pstmt.setInt(3,stock);
            pstmt.setInt(4,id);
            pstmt.executeUpdate();
            System.out.println("商品"+id+"号修改成功");
            conn.commit();
        }catch (Exception e){
            conn.rollback();
            throw e;
        }finally {
            pstmt.close();
            conn.close();
        }
    }
    //删除商品
    public void deleteProductById(int id) throws Exception {
        //定义sql语句（同时删除订单表中内容）
        String sql="delete from product where product_id=?";
        //获取链接
        Connection conn=getConnection();
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            conn.setAutoCommit(false);
            if(checkProductById(id)){
                throw new InvalidDataException("商品"+id+"号不存在");
            }else if(!checkOrder(id)){
                throw new InvalidDataException("商品"+id+"号存在订单，无法删除");
            }
            pstmt.setInt(1,id);
            pstmt.executeUpdate();
            System.out.println("商品"+id+"号删除成功");
            conn.commit();
        }catch (Exception e){
            conn.rollback();
            throw e;
        }finally {
            conn.close();
        }
    }
    //检查商品是否存在(商品名)
    public Boolean checkProductByName(String name) throws Exception {
        Producttest test = new Producttest();
        ArrayList<String> product = test.selectAllProductName();
        boolean status = true;
        for (String string : product) {
            if (string.equals(name)) {
                status = false;
                break;
            }
        }
        return status;
    }
    //检查商品是否存在(商品id)
    public Boolean checkProductById(int id) throws Exception {
        ArrayList<Integer> product = selectAllProductId();
        boolean status = true;
        for (Integer integer : product) {
            if (integer == id) {
                status = false;
                break;
            }
        }
        return status;
    }
    //商品排序（价格）
    public List<Product> productSort() throws Exception {
        List<Product> products=new ArrayList<>();
        //定义sql
        String sql = "SELECT product_id,product_name,product_price,stock FROM product ORDER BY product_price ASC";
        //获取链接
        Connection conn = getConnection();
        //获取stmt对象
        Statement stmt = conn.createStatement();
        try(ResultSet rs = stmt.executeQuery(sql)){
            conn.setAutoCommit(false);
            while (rs.next()){

                Product product=new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setProductPrice(rs.getDouble("product_price"));
                product.setStock(rs.getInt("stock"));
                products.add(product);
            }
        }catch (Exception e){
            conn.rollback();
            throw e;
        }finally {
            stmt.close();
            conn.close();
        }
        return products;
    }
    //通过id得到商品
    public Product getProductById(int id) throws Exception {
        //定义sql语句（同时删除订单表中内容）
        String sql="select product_name,product_price,stock from product where product_id=?;";
        //获取链接
        Connection conn=getConnection();
        Product product=new Product();
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            conn.setAutoCommit(false);
            if(checkProductById(id)){
                throw new InvalidDataException("该商品不存在");
            }
            pstmt.setInt(1,id);
            ResultSet rs =pstmt.executeQuery();
            while (rs.next()){
                product.setProductId(id);
                product.setProductName(rs.getString("product_name"));
                product.setProductPrice(rs.getDouble("product_price"));
                product.setStock(rs.getInt("stock"));
            }
            conn.commit();
        }catch (Exception e){
            conn.rollback();
            throw e;
        }finally {
            conn.close();
        }
        return product;
    }
    //获取库存量
    public int getProductStock(int id) throws Exception {
        //定义sql语句（同时删除订单表中内容）
        String sql="select stock from product where product_id=?;";
        //获取链接
        Connection conn=getConnection();
        int stock;
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            conn.setAutoCommit(false);
            if(checkProductById(id)){
                throw new InvalidDataException("该商品不存在");
            }
            pstmt.setInt(1,id);
            ResultSet rs =pstmt.executeQuery();
            rs.next();
            stock=rs.getInt("stock");
            conn.commit();
        }catch (SQLException e){
            conn.rollback();
            throw e;
        }finally {
            conn.close();
        }
        return stock;
    }
    //检查订单中是否含有该商品
    public Boolean checkOrder(int id) throws Exception {
        ArrayList<Integer> product = selectAllOrderId();
        boolean status = true;
        for (Integer integer : product) {
            if (integer == id) {
                status = false;
                break;
            }
        }
        return status;
    }
}
