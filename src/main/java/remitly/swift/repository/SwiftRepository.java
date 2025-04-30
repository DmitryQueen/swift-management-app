package remitly.swift.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import remitly.swift.entity.Swift;

import java.util.List;
import java.util.Optional;

@Repository
public interface SwiftRepository extends JpaRepository<Swift, Long> {

    Optional<Swift> findBySwiftCode(String swiftCode);

    List<Swift> findAllByCountryISO2(String countryISO2);

    boolean existsBySwiftCode(String swiftCode);

    boolean existsByCountryISO2(String countryISO2);

    @Query(value = """
        SELECT COUNT(*)
        FROM swift_codes
        WHERE SUBSTRING(swift_code FROM 1 FOR 8) = SUBSTRING(:swiftCode FROM 1 FOR 8)
          AND is_headquarter = true
        """, nativeQuery = true)
    int countExistingHeadquarters(String swiftCode);

    @Query(value = """
        SELECT *
        FROM swift_codes
        WHERE SUBSTRING(swift_code FROM 1 FOR 8) = SUBSTRING(:swiftCode FROM 1 FOR 8)
          AND is_headquarter = false
        """, nativeQuery = true)
    List<Swift> findAllBranches(String swiftCode);
}
