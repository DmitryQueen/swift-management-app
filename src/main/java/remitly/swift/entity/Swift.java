package remitly.swift.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "swift_codes")
public class Swift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "country_iso2", nullable = false)
    private String countryISO2;

    @Column(name = "country_name", nullable = false)
    private String countryName;


    @Column(name = "is_headquarter", nullable = false)
    private boolean isHeadquarter;

    @Column(name = "swift_code", nullable = false, unique = true)
    private String swiftCode;
}
