package ch.aaap.assignment.adapter.raw;

import ch.aaap.assignment.model.*;
import ch.aaap.assignment.model.PostalCommunity.ZipCode;
import com.google.common.collect.ImmutableSet;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;

import java.util.Map;
import java.util.Set;

@Immutable
public abstract class RawModel implements Model {

  public abstract Map<String, Canton> getCantonsByCode();

  public abstract Map<String, District> getDistrictsByCode();

  public abstract Map<ZipCode, PostalCommunity> getPostalCommunitiesByCode();

  public abstract Map<String, PoliticalCommunity> getPoliticalCommunitiesByCode();

  @Override
  @Derived
  public Set<PoliticalCommunity> getPoliticalCommunities() {
    return ImmutableSet.copyOf(getPoliticalCommunitiesByCode().values());
  }

  @Override
  @Derived
  public Set<PostalCommunity> getPostalCommunities() {
    return ImmutableSet.copyOf(getPostalCommunitiesByCode().values());
  }

  @Override
  @Derived
  public Set<Canton> getCantons() {
    return ImmutableSet.copyOf(getCantonsByCode().values());
  }

  @Override
  @Derived
  public Set<District> getDistricts() {
    return ImmutableSet.copyOf(getDistrictsByCode().values());
  }
}
