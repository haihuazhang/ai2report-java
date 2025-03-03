package customer.aireport.model;


// Move from RequestAnalyzeUtil.EntityInfo
public  class EntityInfo {
    private final String id;
    private final Boolean isActiveEntity;

    public EntityInfo(String id, Boolean isActiveEntity) {
        this.id = id;
        this.isActiveEntity = isActiveEntity;
    }

    public String getId() {
        return id;
    }

    public Boolean getIsActiveEntity() {
        return isActiveEntity;
    }
}
