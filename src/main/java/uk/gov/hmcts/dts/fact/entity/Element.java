package uk.gov.hmcts.dts.fact.entity;

public abstract class Element {
    public String getDescription(final Element element) {
        final ElementType elementType = element.getAdminType();
        return (elementType == null) ? element.getDescription() : elementType.getDescription();
    }

    abstract String getDescription();

    public String getDescriptionCy(final Element element) {
        final ElementType elementType = element.getAdminType();
        return (elementType == null) ? element.getDescriptionCy() : elementType.getDescriptionCy();
    }

    abstract String getDescriptionCy();

    abstract ElementType getAdminType();
}
