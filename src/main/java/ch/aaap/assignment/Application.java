package ch.aaap.assignment;

import ch.aaap.assignment.adapter.raw.RawModel;
import ch.aaap.assignment.adapter.raw.RawModelFactory;
import ch.aaap.assignment.model.Canton;
import ch.aaap.assignment.model.District;
import ch.aaap.assignment.model.Model;
import ch.aaap.assignment.model.PoliticalCommunity;
import ch.aaap.assignment.raw.CSVPoliticalCommunity;
import ch.aaap.assignment.raw.CSVPostalCommunity;
import ch.aaap.assignment.raw.CSVUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Application {

  private RawModel model = null;

  public Application() {
    initModel();
  }

  public static void main(String[] args) {
    new Application();
  }

  /** Reads the CSVs and initializes a in memory model */
  private void initModel() {
    Set<CSVPoliticalCommunity> politicalCommunities = CSVUtil.getPoliticalCommunities();
    Set<CSVPostalCommunity> postalCommunities = CSVUtil.getPostalCommunities();
    RawModelFactory rawModelFactory = new RawModelFactory();
    model = rawModelFactory.model(politicalCommunities, postalCommunities);
  }
  /** @return model */
  public Model getModel() {
    return model;
  }

  /**
   * @param cantonCode of a canton (e.g. ZH)
   * @return amount of political communities in given canton
   */
  public long getAmountOfPoliticalCommunitiesInCanton(String cantonCode) {
    Canton canton = getCanton(cantonCode);
    return model.getPoliticalCommunities().stream()
        .filter(pc -> pc.getDistrict().getCanton().equals(canton))
        .count();
  }

  /**
   * @param cantonCode of a canton (e.g. ZH)
   * @return amount of districts in given canton
   */
  public long getAmountOfDistrictsInCanton(String cantonCode) {
    Canton canton = getCanton(cantonCode);
    return model.getDistricts().stream().filter(d -> d.getCanton().equals(canton)).count();
  }

  /**
   * @param districtNumber of a district (e.g. 101)
   * @return amount of districts in given canton
   */
  public long getAmountOfPoliticalCommunitiesInDistrict(String districtNumber) {
    District district = getDistrict(districtNumber);
    return model.getPoliticalCommunities().stream()
        .filter(pc -> pc.getDistrict().equals(district))
        .count();
  }

  /**
   * @param zipCode 4 digit zip code
   * @return district that belongs to specified zip code
   */
  public Set<String> getDistrictsForZipCode(String zipCode) {
    return model.getPostalCommunitiesByCode().keySet().stream()
        .filter(k -> k.getZipCode().equals(zipCode))
        .map(z -> model.getPostalCommunitiesByCode().get(z))
        .flatMap(postalCommunity -> postalCommunity.getPoliticalCommunities().stream())
        .map(pc -> pc.getDistrict().getName())
        .collect(Collectors.toSet());
  }

  /**
   * @param postalCommunityName name
   * @return lastUpdate of the political community by a given postal community name
   */
  public LocalDate getLastUpdateOfPoliticalCommunityByPostalCommunityName(
      String postalCommunityName) {
    return model.getPostalCommunities().stream()
        .filter(pc -> pc.getName().equals(postalCommunityName))
        .flatMap(postalCommunity -> postalCommunity.getPoliticalCommunities().stream())
        .map(politicalCommunity -> politicalCommunity.getLastUpdate())
        .max(Comparator.comparing(LocalDate::toEpochDay))
        .get();
  }

  /**
   * https://de.wikipedia.org/wiki/Kanton_(Schweiz)
   *
   * @return amount of canton
   */
  public long getAmountOfCantons() {
    return model.getCantons().size();
  }

  /**
   * https://de.wikipedia.org/wiki/Kommunanz
   *
   * @return amount of political communities without postal communities
   */
  public long getAmountOfPoliticalCommunityWithoutPostalCommunities() {
    Set<PoliticalCommunity> politicalCommunitiesWithPostal =
        model.getPostalCommunities().stream()
            .flatMap(pc -> pc.getPoliticalCommunities().stream())
            .collect(Collectors.toSet());
    return model.getPoliticalCommunities().stream()
        .filter(pc -> !politicalCommunitiesWithPostal.contains(pc))
        .count();
  }

  private Canton getCanton(String cantonCode) {
    return Optional.ofNullable(model.getCantonsByCode().get(cantonCode))
        .orElseThrow(IllegalArgumentException::new);
  }

  private District getDistrict(String districtNumber) {
    return Optional.ofNullable(model.getDistrictsByCode().get(districtNumber))
        .orElseThrow(IllegalArgumentException::new);
  }
}
