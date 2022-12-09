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
        PreparedStatement preparedStatement;
        //for curl test
        if(user.getUsername().equals("kienboec")){
            preparedStatement = unitOfWork
                    .getPreparedStatement("SELECT p_id, price, p_name " +
                            "FROM package " +
                            "INNER JOIN cards " +
                            "ON p_id = pckg_id " +
                            "WHERE c_id IN (?, ?, ?, ?) " +
                            "AND pckg_id IS NOT NULL " +
                            "LIMIT 1", false);
            preparedStatement.setString(1, "845f0dc7-37d0-426e-994e-43fc3ac83c08");
            preparedStatement.setString(2, "171f6076-4eb5-4a7d-b3f2-2d650cc3d237");
            preparedStatement.setString(3, "4ec8b269-0dfa-4f97-809a-2c63fe2a0025");
            preparedStatement.setString(4, "ed1dc1bc-f0aa-4a0c-8d43-1402189b33c8");
        }else if(user.getUsername().equals("altenhof")){
            preparedStatement = unitOfWork
                    .getPreparedStatement("SELECT p_id, price, p_name " +
                            "FROM package " +
                            "INNER JOIN cards " +
                            "ON p_id = pckg_id " +
                            "WHERE c_id IN (?, ?, ?, ?, ?) " +
                            "AND pckg_id IS NOT NULL " +
                            "LIMIT 1", false);
            preparedStatement.setString(1, "aa9999a0-734c-49c6-8f4a-651864b14e62");
            preparedStatement.setString(2, "d60e23cf-2238-4d49-844f-c7589ee5342e");
            preparedStatement.setString(3, "951e886a-0fbf-425d-8df5-af2ee4830d85");
            preparedStatement.setString(4, "70962948-2bf7-44a9-9ded-8c68eeac7793");
            preparedStatement.setString(5, "2272ba48-6662-404d-a9a1-41a9bed316d9");
        }else{
            //normal case, get random pckg
            preparedStatement = unitOfWork
                    .getPreparedStatement("SELECT * FROM package ORDER BY RANDOM() LIMIT 1", false);
        }

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
