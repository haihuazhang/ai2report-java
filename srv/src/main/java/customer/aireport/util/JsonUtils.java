package customer.aireport.util;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
// import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionFunction;
import cds.gen.chatservice.*;
import customer.aireport.dto.FieldSummary;
import customer.aireport.exception.BusinessException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JsonUtils {
    private final ObjectMapper objectMapper;

    public JsonUtils() {
        this.objectMapper = new ObjectMapper();
    }

    public JsonNode parseJson(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw BusinessException.parsingError("JSON_Result", e);
        }
    }

    public void setReportText(Reports reports, JsonNode rootNode) {
        reports.setText(rootNode.get("Reports").get("Text").asText());
    }

    public List<ReportFields> parseReportFields(JsonNode rootNode) {
        List<ReportFields> fieldsList = new ArrayList<>();
        JsonNode fieldsNode = rootNode.get("fields");

        if (fieldsNode != null && fieldsNode.isArray()) {
            for (JsonNode arrayItem : fieldsNode) {
                ReportFields field = createReportField(arrayItem);
                fieldsList.add(field);
            }
        }
        return fieldsList;
    }

    public ReportFields createReportField(JsonNode arrayItem) {
        ReportFields field = ReportFields.create();
        field.setCategory(getStringWithDefault(arrayItem, "category", "default_category"));
        field.setTabFdPos(getIntWithDefault(arrayItem, "TabFdPos", 0));
        field.setParamText(getStringWithDefault(arrayItem, "ParamText", ""));
        field.setFieldType(getStringWithDefault(arrayItem, "FieldType", "string"));
        field.setDisplay(getStringWithDefault(arrayItem, "Display", ""));
        field.setEnterable(getStringWithDefault(arrayItem, "Enterable", ""));
        field.setObligatory(getStringWithDefault(arrayItem, "Obligatory", ""));
        field.setValueHelp(getStringWithDefault(arrayItem, "ValueHelp", ""));
        field.setToEntityText(getStringWithDefault(arrayItem, "ToEntityText", ""));
        field.setToEntity(getStringWithDefault(arrayItem, "ToEntity", ""));
        field.setToFieldText(getStringWithDefault(arrayItem, "ToFieldText", ""));
        field.setToField(getStringWithDefault(arrayItem, "ToField", ""));
        field.setIsKey(getStringWithDefault(arrayItem, "IsKey", ""));
        field.setRequiresCalculation(getStringWithDefault(arrayItem, "RequiresCalculation", ""));
        field.setCaculationLogic(getStringWithDefault(arrayItem, "CaculationLogic", ""));
        field.setValueHelpTable(getStringWithDefault(arrayItem, "ValueHelpTable", ""));
        field.setValueHelpField(getStringWithDefault(arrayItem, "ValueHelpField", ""));
        field.setSeq(getIntWithDefault(arrayItem, "Seq", 0));
        return field;
    }

    public Pcls createPcl(JsonNode arrayItem) {
        Pcls pcl = Pcls.create();
        pcl.setNum(getStringWithDefault(arrayItem, "num", ""));
        pcl.setCategory(getStringWithDefault(arrayItem, "category", "default_category"));
        pcl.setScene(getStringWithDefault(arrayItem, "scene", ""));
        pcl.setExpectedResult(getStringWithDefault(arrayItem, "expectedResult", ""));
        return pcl;
    }

    public String convertFieldsToJson(List<ReportFields> fields) {
        try {
            return objectMapper.writeValueAsString(fields.stream()
                    .map(f -> new FieldSummary(
                            f.getCategory(), f.getTabFdPos(), f.getParamText(),
                            f.getFieldType(), f.getDisplay(), f.getEnterable(),
                            f.getObligatory(), f.getValueHelp(), f.getToEntityText(),
                            f.getToEntity(), f.getToFieldText(), f.getToField(),
                            f.getIsKey(), f.getRequiresCalculation(),
                            f.getCaculationLogic(), f.getValueHelpTable(),
                            f.getValueHelpField(), f.getSeq()))
                    .collect(Collectors.toList()));
        } catch (JsonProcessingException e) {
            throw BusinessException.parsingError("Field_To_JSON", e);
        }
    }

    public <T> T parseFunction(String jsonString, String errorMessage, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            throw BusinessException.parsingError(errorMessage, e);
        }
    }

    private String getStringWithDefault(JsonNode node, String field, String defaultValue) {
        JsonNode value = node.path(field);
        return value.isMissingNode() ? defaultValue : value.asText();
    }

    private int getIntWithDefault(JsonNode node, String field, int defaultValue) {
        JsonNode value = node.path(field);
        return value.isMissingNode() ? defaultValue : value.asInt();
    }
}
