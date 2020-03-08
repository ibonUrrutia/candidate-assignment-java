package ch.aaap.assignment.adapter.raw;

import ch.aaap.assignment.model.*;
import ch.aaap.assignment.model.PostalCommunity.ZipCode;
import ch.aaap.assignment.raw.CSVPoliticalCommunity;
import ch.aaap.assignment.raw.CSVPostalCommunity;
import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RawModelFactory {

  private static final int NUMBER_OF_POLITICAL_COMMUNITIES_IN_SWITZERLAND = 2215;
  private static final int NUMBER_OF_POSTAL_COMMUNITIES_IN_SWITZERLAND = 4064;
  private static final int NUMBER_OF_DISTRICTS_IN_SWITZERLAND = 143;
  private static final int NUMBER_OF_CANTONS_IN_SWITZERLAND = 26;

  private final Map<String, PoliticalCommunity> politicalCommunities;
  private final Map<ZipCode, PostalCommunity> postalCommunities;
  private final Map<String, District> districts;
  private final Map<String, Canton> cantons;

  public RawModelFactory() {
    politicalCommunities = new HashMap<>(NUMBER_OF_POLITICAL_COMMUNITIES_IN_SWITZERLAND);
    districts = new HashMap<>(NUMBER_OF_DISTRICTS_IN_SWITZERLAND);
    cantons = new HashMap<>(NUMBER_OF_CANTONS_IN_SWITZERLAND);
    postalCommunities = new HashMap<>(NUMBER_OF_POSTAL_COMMUNITIES_IN_SWITZERLAND);
  }

  public RawModel model(
      Set<CSVPoliticalCommunity> csvPoliticalCommunities,
      Set<CSVPostalCommunity> csvPostalCommunities) {
    csvPoliticalCommunities.forEach(csvpc -> politicalCommunity(csvpc));
    csvPostalCommunities.forEach(csvpc -> postalCommunity(csvpc));

    return ImmutableRawModel.builder()
        .putAllCantonsByCode(cantons)
        .putAllDistrictsByCode(districts)
        .putAllPoliticalCommunitiesByCode(politicalCommunities)
        .putAllPostalCommunitiesByCode(postalCommunities)
        .build();
  }

  private PoliticalCommunity politicalCommunity(CSVPoliticalCommunity csvPoliticalCommunity) {
    return politicalCommunities.computeIfAbsent(
        csvPoliticalCommunity.getNumber(),
        k ->
            ImmutablePoliticalCommunity.builder()
                .number(csvPoliticalCommunity.getNumber())
                .name(csvPoliticalCommunity.getName())
                .shortName(csvPoliticalCommunity.getShortName())
                .lastUpdate(csvPoliticalCommunity.getLastUpdate())
                .district(district(csvPoliticalCommunity))
                .build());
  }

  private PoliticalCommunity politicalCommunity(String politicalCommunityNumber) {
    return Optional.ofNullable(politicalCommunities.get(politicalCommunityNumber))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "A political community does not exist for: " + politicalCommunityNumber));
  }

  private District district(CSVPoliticalCommunity csvPoliticalCommunity) {
    return districts.computeIfAbsent(
        csvPoliticalCommunity.getDistrictNumber(),
        k ->
            ImmutableDistrict.builder()
                .number(csvPoliticalCommunity.getDistrictNumber())
                .name(csvPoliticalCommunity.getDistrictName())
                .canton(canton(csvPoliticalCommunity))
                .build());
  }

  private Canton canton(CSVPoliticalCommunity csvPoliticalCommunity) {
    return cantons.computeIfAbsent(
        csvPoliticalCommunity.getCantonCode(),
        k ->
            ImmutableCanton.builder()
                .code(csvPoliticalCommunity.getCantonCode())
                .name(csvPoliticalCommunity.getCantonName())
                .build());
  }

  private PostalCommunity postalCommunity(CSVPostalCommunity csvPostalCommunity) {
    ZipCode zipCode =
        ImmutableZipCode.builder()
            .zipCode(csvPostalCommunity.getZipCode())
            .zipCodeAddition(csvPostalCommunity.getZipCodeAddition())
            .build();
    return postalCommunities.merge(
        zipCode,
        ImmutablePostalCommunity.builder()
            .zipCode(zipCode)
            .name(csvPostalCommunity.getName())
            .addPoliticalCommunities(
                politicalCommunity(csvPostalCommunity.getPoliticalCommunityNumber()))
            .build(),
        (old, current) ->
            ImmutablePostalCommunity.copyOf(old)
                .withPoliticalCommunities(
                    ImmutableSet.<PoliticalCommunity>builder()
                        .addAll(old.getPoliticalCommunities())
                        .addAll(current.getPoliticalCommunities())
                        .build()));
  }
}
