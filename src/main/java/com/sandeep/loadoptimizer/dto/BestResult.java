package com.sandeep.loadoptimizer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BestResult {
    private long payout = -1;
    private long mask = 0;
    private int weight = 0;
    private int volume = 0;
}