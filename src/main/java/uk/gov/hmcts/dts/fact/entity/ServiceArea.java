package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@SuppressWarnings({"PMD.ExcessiveParameterList"})
@Entity
@Table(name = "search_servicearea")
@Getter
@Setter
public class ServiceArea {

    private static final String REGIONAL = "regional";

    @Id
    private Integer id;
    private String name;
    private String nameCy;
    private String description;
    private String descriptionCy;
    private String slug;
    private String onlineUrl;
    private String onlineText;
    private String onlineTextCy;
    private String type;
    private String text;
    private String textCy;
    private String catchmentMethod;

    @OneToMany(mappedBy = "servicearea")
    private List<ServiceAreaCourt> serviceAreaCourts;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "area_of_law_id")
    private AreaOfLaw areaOfLaw;

    public ServiceArea() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public ServiceArea(int id, String name, String nameCy, String description, String descriptionCy, String slug, String onlineUrl,
                       String onlineText, String onlineTextCy, String type, String text, String textCy, String catchmentMethod) {
        this.id = id;
        this.name = name;
        this.nameCy = nameCy;
        this.description = description;
        this.descriptionCy = descriptionCy;
        this.slug = slug;
        this.onlineUrl = onlineUrl;
        this.onlineText = onlineText;
        this.onlineTextCy = onlineTextCy;
        this.type = type;
        this.text = text;
        this.textCy = textCy;
        this.catchmentMethod = catchmentMethod;
    }

    public boolean isRegional() {
        return this.serviceAreaCourts.stream()
            .anyMatch(serviceAreaCourt -> REGIONAL.equals(serviceAreaCourt.getCatchmentType()));
    }
}
