package fi.digitraffic.graphql.rail.querydsl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import graphql.schema.SelectedField;

@Service
public class FieldsToSQLFieldsService {

    public Expression<?>[] getFields(EntityPath entityTable, List<SelectedField> fields, List<Expression<?>> extraSQLFields) {
        List<Expression<?>> paths = new ArrayList<>();
        paths.addAll(extraSQLFields);
        for (SelectedField field : fields) {
            if (!field.getQualifiedName().contains("/") &&
                    field.getSelectionSet().getFields().isEmpty() &&
                    !field.getQualifiedName().equals("__typename")) {
                paths.add(Expressions.path(Tuple.class, entityTable, field.getName()));
            }
        }
        return paths.toArray(new Expression[0]);
    }


}
