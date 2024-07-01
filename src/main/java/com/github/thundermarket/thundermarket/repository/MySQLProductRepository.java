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
    public List<Product> findAll(Long cursorId, int limit) {
        String sql = "SELECT * FROM products WHERE id > ? ORDER BY id ASC LIMIT ?";
        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, cursorId);
            pstmt.setInt(2, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    String status = rs.getString("status");
                    products.add(new Product.Builder()
                            .withId(id)
                            .withName(name)
                            .withPrice(price)
                            .withStatus(status)
                            .build()
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fetching products failed", e);
        }

        return products;
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM products";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Counting products failed", e);
        }
    }
}
