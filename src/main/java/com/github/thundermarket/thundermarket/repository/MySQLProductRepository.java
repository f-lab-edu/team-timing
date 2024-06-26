package com.github.thundermarket.thundermarket.repository;

import com.github.thundermarket.thundermarket.domain.Product;
import com.github.thundermarket.thundermarket.domain.User;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MySQLProductRepository implements ProductRepository {

    private final DataSource dataSource;

    public MySQLProductRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void releaseConnection(Connection conn) {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    @Override
    public Product save(Product product) {
        String sql = "INSERT INTO products (name, price, status) VALUES (?, ?, ?)";
        Connection conn = null;
        long generatedKey = 0L;

        try {
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, product.getName());
                ps.setInt(2, product.getPrice());
                ps.setString(3, product.getStatus());
                int affectedRows = ps.executeUpdate();

                if (affectedRows == 0) {
                    throw new RuntimeException("Create products failed, no affectedRows");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        throw new RuntimeException("Create products failed, no generatedKeys");
                    }
                    generatedKey = generatedKeys.getLong(1);
                }
                return new Product.Builder(product)
                        .withId(generatedKey)
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Create products failed", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT * FROM products";
        Connection conn = null;

        try {
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ResultSet resultSet = ps.executeQuery();
                List<Product> products = new ArrayList<>();

                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    int price = resultSet.getInt("price");
                    String status = resultSet.getString("status");
                    products.add(new Product.Builder()
                                    .withId(id)
                                    .withName(name)
                                    .withPrice(price)
                                    .withStatus(status)
                                    .build()
                    );
                }
                return products;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find all products failed", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
