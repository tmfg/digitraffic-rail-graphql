package fi.digitraffic.graphql.rail.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import graphql.execution.AbortExecutionException;
import graphql.language.Field;
import graphql.language.FragmentDefinition;
import graphql.language.FragmentSpread;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import graphql.schema.DataFetchingEnvironment;

public class GraphQLFieldSelectionUtil {

    public static List<Field> getSelectionSet(final DataFetchingEnvironment dataFetchingEnvironment) {
        List<Field> output = new ArrayList<>();
        addFieldsToOutput(dataFetchingEnvironment.getField().getSelectionSet(), output, dataFetchingEnvironment.getFragmentsByName());
        return output;
    }

    private static void addFieldsToOutput(final SelectionSet selectionSet, final List<Field> output, final Map<String, FragmentDefinition> fragmentsByName) {
        final List<Selection> selections = selectionSet.getSelections();
        for (final Selection selection : selections) {
            if (selection instanceof Field field) {
                output.add(field);
            } else if (selection instanceof FragmentSpread fragmentSpread) {
                addFieldsToOutput(fragmentsByName.get(fragmentSpread.getName()).getSelectionSet(), output, fragmentsByName);
            } else {
                throw new AbortExecutionException("Unknown selection type: " + selection);
            }
        }
    }
}
