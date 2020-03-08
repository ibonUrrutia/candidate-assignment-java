package ch.aaap.assignment.model;

import org.immutables.value.Value.Immutable;

@Immutable
public interface District {

  String getNumber();

  String getName();

  Canton getCanton();
}
