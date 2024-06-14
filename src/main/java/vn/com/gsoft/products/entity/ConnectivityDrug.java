package vn.com.gsoft.products.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ConnectivityDrug")
public class ConnectivityDrug extends BaseEntity{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Code")
    private String code;
    @Column(name = "Name")
    private String name;
    @Column(name = "RegisteredNo")
    private String registeredNo;
    @Column(name = "ActiveSubstance")
    private String activeSubstance;
    @Column(name = "Contents")
    private String contents;
    @Column(name = "PackingWay")
    private String packingWay;
    @Column(name = "Manufacturer")
    private String manufacturer;
    @Column(name = "CountryOfManufacturer")
    private String countryOfManufacturer;
    @Column(name = "UnitName")
    private String unitName;
    @Column(name = "DrugStoreId")
    private String drugStoreId;
    @Column(name = "DrugId")
    private Long drugId;
    @Column(name = "ConnectivityStatusId")
    private Long connectivityStatusId;
    @Column(name = "CountryId")
    private Long countryId;
    @Column(name = "ConnectivityId")
    private String connectivityId;
    @Column(name = "ConnectivityResult")
    private String connectivityResult;
    @Column(name = "ConnectivityDateTime")
    private Date connectivityDateTime;
    @Column(name = "StoreId")
    private Long storeId;
    @Column(name = "ConnectivityTypeId")
    private Long connectivityTypeId;
    @Column(name = "DosageForms")
    private String dosageForms;
    @Column(name = "SmallestPackingUnit")
    private String smallestPackingUnit;
    @Column(name = "DeclaredPrice")
    private BigDecimal declaredPrice;
    @Column(name = "WholesalePrice")
    private BigDecimal wholesalePrice;
    @Column(name = "Importers")
    private String importers;
    @Column(name = "OrganizeDeclaration")
    private String organizeDeclaration;
    @Column(name = "CountryRegistration")
    private String countryRegistration;
    @Column(name = "AddressRegistration")
    private String addressRegistration;
    @Column(name = "AddressManufacture")
    private String addressManufacture;
    @Column(name = "Classification")
    private String classification;
    @Column(name = "Identifier")
    private String identifier;
    @Column(name = "PrescriptionTypeId")
    private Long prescriptionTypeId;
    @Column(name = "ForWholesale")
    private Boolean forWholesale;

    @Transient
    private Boolean updatable;
    @Transient
    private String connectivityName;
    @Transient
    private Long retailUnitId;
    @Transient
    private String retailUnitName;
    @Transient
    private String maThuoc;
    @Transient
    private String tenThuoc;
    @Transient
    private Long connectivityDrugID;
    @Transient
    private String connectivityCode;
    @Transient
    private String connectivityDrugName;
    @Transient
    private BigDecimal connectivityDrugFactor;
    @Transient
    private Thuocs thuocs;
    @Transient
    private Long itemTypeId;
}

