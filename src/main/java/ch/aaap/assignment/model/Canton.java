package ch.aaap.assignment.model;

import org.immutables.value.Value.Immutable;

@Immutable
public interface Canton {

  String getCode();

  String getName();
}
