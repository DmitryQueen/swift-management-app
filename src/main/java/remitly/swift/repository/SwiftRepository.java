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

    @Query(value = """
        select * from swift_codes
        WHERE substring(swift_code FROM 1 FOR 8) = substring(:swiftCode FROM 1 FOR 8) AND is_headquarter = false;
        """, nativeQuery = true)
    List<Swift> findAllBranches(String swiftCode);

    List<Swift> findAllByCountryISO2(String countryISO2);

    void deleteSwiftBySwiftCode(String swiftCode);

    boolean existsBySwiftCode(String swiftCode);
}
