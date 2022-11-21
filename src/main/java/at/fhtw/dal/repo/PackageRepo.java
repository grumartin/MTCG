package at.fhtw.dal.repo;

import at.fhtw.dal.UnitOfWork;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.models.Pckg;
import at.fhtw.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageRepo {
    public int addPackage(Pckg pckg, UnitOfWork unitOfWork) {
        try {
            PreparedStatement preparedStatement = unitOfWork
                    .getPreparedStatement("INSERT INTO package(price, p_name) VALUES(?,?)", true);
            preparedStatement.setInt(1, pckg.getPrice());
            preparedStatement.setString(2, pckg.getName());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKey = preparedStatement.getGeneratedKeys()) {
                    if (generatedKey.next())
                        return generatedKey.getInt(1);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return -1;
        }
        return -1;
    }

    public ResultSet acquirePackage(User user, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("SELECT * FROM package ORDER BY RANDOM() LIMIT 1", false);

        return preparedStatement.executeQuery();
    }

    public void deletePackage(Pckg pckg, UnitOfWork unitOfWork) throws Exception {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("DELETE FROM package WHERE p_id = ?", false);
        preparedStatement.setInt(1, pckg.getP_id());

        if(preparedStatement.executeUpdate() == 0)
            throw new RuntimeException("Package delete failed!");
    }

}
