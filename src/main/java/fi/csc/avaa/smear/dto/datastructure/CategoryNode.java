package fi.csc.avaa.smear.dto.datastructure;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CategoryNode {

    @EqualsAndHashCode.Include
    private String id;
    private String name;
    @Setter
    private List<VariableNode> variables;
}
