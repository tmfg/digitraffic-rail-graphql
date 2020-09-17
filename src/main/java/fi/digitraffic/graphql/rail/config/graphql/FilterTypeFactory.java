package fi.digitraffic.graphql.rail.config.graphql;

import org.springframework.stereotype.Service;

import graphql.language.FieldDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.TypeDefinition;
import graphql.language.TypeName;

@Service
public class FilterTypeFactory {
    public ObjectTypeDefinition createType(String name, TypeDefinition typeDefinition) {
        ObjectTypeDefinition.Builder builder = ObjectTypeDefinition.newObjectTypeDefinition().name(name + "Filter");

        ObjectTypeDefinition objectTypeDefinition = (ObjectTypeDefinition) typeDefinition;
        for (FieldDefinition fieldDefinition : objectTypeDefinition.getFieldDefinitions()) {
            if (fieldDefinition.getType() instanceof TypeName && ((TypeName) fieldDefinition.getType()).getName().equals("Boolean")) {
                FieldDefinition def = FieldDefinition.newFieldDefinition().name(fieldDefinition.getName() + "Filter").type(TypeName.newTypeName("BooleanFilter").build()).build();
                builder = builder.fieldDefinition(def);
            }
        }

        return builder.build();
    }
}
