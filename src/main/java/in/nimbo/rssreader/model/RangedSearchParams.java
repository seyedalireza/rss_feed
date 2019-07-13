package in.nimbo.rssreader.model;

import lombok.*;

@Builder
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class RangedSearchParams {
    private String startDate;
    private String endDate;
    private SearchParams params;
}
