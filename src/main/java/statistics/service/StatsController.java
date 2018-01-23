package statistics.service;

import statistics.core.BaseStats;
import statistics.core.Stats;
import statistics.core.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Exposes the RESTful service
 */
@RestController
@EnableAutoConfiguration
public class StatsController {

    @Autowired
    SyncStatsCollector collector;

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public ResponseEntity<BaseStats> statistics() {
        Stats result = collector.getAggregated();

        // It is a bit unclear from the specification what to return in case there is no core to show.
        // I decided to return just {"count": 0} in order not to confuse the consumer with
        // initial values from the rest of the fields. The missing fields can be treated as nulls.
        if (result.getCount() == 0) {
            return new ResponseEntity<>(new BaseStats(), HttpStatus.OK);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<Void> postTransaction(@RequestBody Transaction txn) {
        HttpStatus status = HttpStatus.OK;
        if (!collector.observe(txn)) {
            status = HttpStatus.NO_CONTENT;
        }
        return new ResponseEntity<>(status);
    }

}