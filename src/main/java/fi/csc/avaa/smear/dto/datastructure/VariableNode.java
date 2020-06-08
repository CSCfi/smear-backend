package fi.csc.avaa.smear.dto.datastructure;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VariableNode {

    private String tablevariable;
    private String title;
}
