package graphqlscope.graphql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.model.AnimalTO;
import graphqlscope.graphql.model.CountryTO;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.repositories.TrainRepository;

@Component
public class GraphQLDataFetchers {

    @Autowired
    private TrainRepository trainRepository;

    private static final Logger LOG = LoggerFactory.getLogger(GraphQLDataFetchers.class);

    private static final Map<String, String> COUNTRIES = Map.of(
            "A", "Argentina",
            "B", "Brazil",
            "C", "Chile"
    );

    public DataFetcher animalsFetcher() {
        return dataFetchingEnvironment -> {
            List<AnimalTO> animals = List.of(
                    new AnimalTO("1", "tapir", "black&white", List.of("A", "B"), null),
                    new AnimalTO("2", "party parrot", "rainbow", List.of("A", "C"), null)
            );
            LOG.info("Returning {} animals.", animals.size());
            return animals;
        };
    }

    public DataFetcher animalCountriesFetcher() {
        return dataFetchingEnvironment -> {
            AnimalTO animal = dataFetchingEnvironment.getSource();
            Collection<String> countryIds = animal.getCountryIds();

            DataLoaderRegistry dataLoaderRegistry = dataFetchingEnvironment.getContext();
            DataLoader<String, CountryTO> countryLoader = dataLoaderRegistry.getDataLoader("countries");

            return countryLoader.loadMany(new ArrayList<>(countryIds));
        };
    }

    public BatchLoader<String, CountryTO> countryBatchLoader() {
        return countryIds ->
                CompletableFuture.supplyAsync(() -> {
                            List<CountryTO> countries = countryIds
                                    .stream()
                                    .map(COUNTRIES::get)
                                    .map(country -> new CountryTO(country, country))
                                    .collect(Collectors.toList());
                            LOG.info("Returning {} countries.", countries.size());
                            return countries;
                        }
                );
    }

    public DataFetcher trainFetcher() {
        return dataFetchingEnvironment -> {
            Integer trainNumber = dataFetchingEnvironment.getArgument("trainNumber");
            LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");

            return trainRepository.findById(new TrainId(trainNumber, departureDate))
                    .map(s -> new TrainTO(s.id.trainNumber.intValue(), s.id.departureDate.toString(), s.version.toString()));
        };
    }
}
