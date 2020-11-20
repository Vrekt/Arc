package arc.exemption.cache;

import arc.check.CheckCategory;
import arc.check.CheckType;

public final class CacheType {

    private final CheckCategory category;
    private final CheckType checkType;

    public CacheType(CheckCategory category, CheckType checkType) {
        this.category = category;
        this.checkType = checkType;
    }

    public CheckCategory category() {
        return category;
    }

    public CheckType checkType() {
        return checkType;
    }
}
