package com.omisoft.hsracer.ws.protocol.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.omisoft.hsracer.ws.protocol.enums.RaceStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by nslavov on 5/18/17.
 */
@Getter
@Setter
@ToString
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
public class RaceStatusDTO implements Parcelable {

  public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    public RaceStatusDTO createFromParcel(Parcel in) {
      return new RaceStatusDTO(in);
    }

    public RaceStatusDTO[] newArray(int size) {
      return new RaceStatusDTO[size];
    }
  };
  private Long raceId;
  private RaceStatus raceStatus;
  private List<RacerSummary> summaryList;




  public RaceStatusDTO(Parcel in) {
    summaryList = new ArrayList<>();
    raceId = in.readLong();
    raceStatus = RaceStatus.valueOf(in.readString());
    in.readList(summaryList, RacerSummary.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(raceId);
    dest.writeString(raceStatus.name());
    dest.writeList(summaryList);
  }
}
