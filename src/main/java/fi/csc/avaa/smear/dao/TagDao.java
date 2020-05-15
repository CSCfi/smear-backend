package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Tag;
import org.jooq.DSLContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static org.jooq.impl.DSL.field;

@ApplicationScoped
public class TagDao {

    @Inject
    DSLContext create;

    public List<Tag> findByVariableIds(List<String> variableIds) {
        return create
                .select()
                .from("Tags")
                .join("variableTags")
                .on(field("Tags.tagID").eq(field("variableTags.TagID")))
                .where(field("variableTags.variableID").in(variableIds))
                .fetch(Tag::from);
    }

    public List<Tag> findById(Integer id) {
        return create
                .select()
                .from("Tags")
                .where(field("tagID").eq(id))
                .fetch(Tag::from);
    }
}
