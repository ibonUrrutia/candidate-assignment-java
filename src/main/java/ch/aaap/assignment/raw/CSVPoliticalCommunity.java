package ch.aaap.assignment.raw;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CSVPoliticalCommunity {

  // GDENR
  private String number;

  // GDENAME
  private String name;

  // GDENAMK
  private String shortName;

  // GDEKT
  private String cantonCode;

  // GDEKTNA
  private String cantonName;

  // GDEBZNR
  private String districtNumber;

  // GDEBZNA
  private String districtName;

  // GDEMUTDAT
  private LocalDate lastUpdate;
}
