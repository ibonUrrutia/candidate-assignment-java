package ch.aaap.assignment.model;

import org.immutables.value.Value.Immutable;

import java.time.LocalDate;

@Immutable
public interface PoliticalCommunity {

  String getNumber();

  String getName();

  String getShortName();

  LocalDate getLastUpdate();

  District getDistrict();
}
