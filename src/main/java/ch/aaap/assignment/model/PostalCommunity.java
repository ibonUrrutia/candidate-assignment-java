package ch.aaap.assignment.model;

import org.immutables.value.Value.Immutable;

import java.util.Set;

@Immutable
public interface PostalCommunity {

  @Immutable
  interface ZipCode {
    String getZipCode();

    String getZipCodeAddition();
  }

  ZipCode getZipCode();

  String getName();

  Set<PoliticalCommunity> getPoliticalCommunities();
}
