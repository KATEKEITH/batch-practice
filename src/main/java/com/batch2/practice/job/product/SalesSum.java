package com.batch2.practice.job.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class SalesSum {

    private LocalDate orderDate;
    private Long amountSum;

}
