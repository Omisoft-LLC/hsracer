package com.omisoft.hsracer.dto;


import java.util.List;
import lombok.Getter;
import lombok.ToString;

/**
 * Pagination wrapper for results
 * Created by developer on 22.11.17.
 */
@Getter
@ToString
public class ResultsPaginationDTO {

  private int total;
  private int per_page;
  private int current_page;
  private int last_page;
  private List<RaceResultDTO> data;
  private String sortField;
  private String field;
  private String sort;
  private int offset;
}
