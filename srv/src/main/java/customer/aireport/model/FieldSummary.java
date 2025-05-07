package customer.aireport.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FieldSummary(
    @JsonProperty("Category") String category,
    @JsonProperty("TabFdPos") int tabFdPos,
    @JsonProperty("ParamText") String paramText,
    @JsonProperty("FieldType") String fieldType,
    @JsonProperty("Display") String display,
    @JsonProperty("Enterable") String enterable,
    @JsonProperty("Obligatory") String obligatory,
    @JsonProperty("ValueHelp") String valueHelp,
    @JsonProperty("ToEntityText") String toEntityText,
    @JsonProperty("ToEntity") String toEntity,
    @JsonProperty("ToFieldText") String toFieldText,
    @JsonProperty("ToField") String toField,
    @JsonProperty("IsKey") String isKey,
    @JsonProperty("RequiresCalculation") String requiresCalculation,
    @JsonProperty("CaculationLogic") String caculationLogic,
    @JsonProperty("ValueHelpTable") String valueHelpTable,
    @JsonProperty("ValueHelpField") String valueHelpField,
    @JsonProperty("Seq") int seq
) {} 