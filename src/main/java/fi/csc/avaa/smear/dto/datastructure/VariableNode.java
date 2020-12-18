package fi.csc.avaa.smear.dto.datastructure;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VariableNode {

    private long variableId;
    private String tablevariable;
    private String title;
    private Integer sortOrder;
}
