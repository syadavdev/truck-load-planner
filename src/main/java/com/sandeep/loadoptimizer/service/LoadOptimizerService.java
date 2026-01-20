package com.sandeep.loadoptimizer.service;

import com.sandeep.loadoptimizer.dto.BestResult;
import com.sandeep.loadoptimizer.dto.Order;
import com.sandeep.loadoptimizer.dto.Truck;
import com.sandeep.loadoptimizer.dto.TruckLoadResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LoadOptimizerService {

    public TruckLoadResponse optimize(Truck truck, List<Order> allOrders) throws InterruptedException {
        //1.Group by Lane
        Map<String, List<Order>> ordersByLane = allOrders.stream()
                .filter(o -> !LocalDate.parse(o.pickupDate()).isAfter(LocalDate.parse(o.deliveryDate())))
                .collect(Collectors.groupingBy(o -> o.origin() + "|" + o.destination()));

        TruckLoadResponse globalBestResponse = null;

        //2.Evaluate each lane
        for (List<Order> laneOrders : ordersByLane.values()) {

            //Separate Hazmat vs Standard
            List<Order> hazmat = laneOrders.stream().filter(Order::isHazmat).toList();
            List<Order> standard = laneOrders.stream().filter(o -> !o.isHazmat()).toList();

            //Process Standard
            BestResult stdBest = new BestResult();
            solveByBacktracking(0, 0, 0, 0, 0, standard, truck, getSuffixSums(standard), stdBest);
            TruckLoadResponse stdRes = buildResponse(truck, standard, stdBest);

            //Process Hazmat
            BestResult hzBest = new BestResult();
            solveByBacktracking(0, 0, 0, 0, 0, hazmat, truck, getSuffixSums(hazmat), hzBest);
            TruckLoadResponse hzRes = buildResponse(truck, hazmat, hzBest);

            //Compare each Max Payouts of this lane
            TruckLoadResponse laneWinner = (hzRes.totalPayoutCents() > stdRes.totalPayoutCents()) ? hzRes : stdRes;

            // Update Global
            if (globalBestResponse == null || laneWinner.totalPayoutCents() > globalBestResponse.totalPayoutCents()) {
                globalBestResponse = laneWinner;
            }
        }

        return globalBestResponse;
    }

    /**
     * Recursive backtracking with bit-masking
     */
    private void solveByBacktracking(int idx,
                                     int weight,
                                     int volume,
                                     long payout,
                                     long mask,
                                     List<Order> orders,
                                     Truck truck,
                                     long[] suffix,
                                     BestResult best) throws InterruptedException {

        //if controller reached timeout limit
        if (Thread.currentThread().isInterrupted()) throw new RuntimeException("Timeout");

        //Checking against pre-calculated values
        if (payout > best.getPayout()) {
            best.setPayout(payout);
            best.setMask(mask);
            best.setWeight(weight);
            best.setVolume(volume);
        }

        if (idx == orders.size() || (payout + suffix[idx] <= best.getPayout()))
            return;

        Order o = orders.get(idx);
        if (weight + o.weightLbs() <= truck.maxWeightLbs() && volume + o.volumeCuft() <= truck.maxVolumeCuft()) {
            solveByBacktracking(idx + 1,
                    weight + o.weightLbs(),
                    volume + o.volumeCuft(),
                    payout + o.payoutCents(),
                    mask | (1L << idx),
                    orders,
                    truck,
                    suffix,
                    best);
        }

        solveByBacktracking(idx + 1, weight, volume, payout, mask, orders, truck, suffix, best);
    }


    /**
     * Pre-calculating payouts at each index
     */
    private long[] getSuffixSums(List<Order> orders) {
        long[] suffix = new long[orders.size() + 1];
        for (int i = orders.size() - 1; i >= 0; i--) {
            suffix[i] = suffix[i + 1] + orders.get(i).payoutCents();
        }
        return suffix;
    }


    /**
     * Just building response with mask
     */
    private TruckLoadResponse buildResponse(Truck truck, List<Order> orders, BestResult best) {
        List<String> selectedIds = new ArrayList<>();
        //Selecting Ids of order which are in bit-mask variable
        for (int i = 0; i < orders.size(); i++) {
            if ((best.getMask() & (1L << i)) != 0) {
                selectedIds.add(orders.get(i).id());
            }
        }

        double wUtil = (double) best.getWeight() / truck.maxWeightLbs() * 100.0;
        double vUtil = (double) best.getVolume() / truck.maxVolumeCuft() * 100.0;

        return new TruckLoadResponse(
                truck.id(), selectedIds, Math.max(0, best.getPayout()), best.getWeight(), best.getVolume(),
                Math.round(wUtil * 100.0) / 100.0,
                Math.round(vUtil * 100.0) / 100.0
        );
    }
}
