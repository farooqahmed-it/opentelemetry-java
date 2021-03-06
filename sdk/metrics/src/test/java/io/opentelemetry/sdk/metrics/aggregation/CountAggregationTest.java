/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.metrics.aggregation;

import static org.assertj.core.api.Assertions.assertThat;

import io.opentelemetry.api.common.Labels;
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo;
import io.opentelemetry.sdk.metrics.accumulation.LongAccumulation;
import io.opentelemetry.sdk.metrics.aggregator.AggregatorHandle;
import io.opentelemetry.sdk.metrics.aggregator.CountAggregator;
import io.opentelemetry.sdk.metrics.common.InstrumentDescriptor;
import io.opentelemetry.sdk.metrics.common.InstrumentType;
import io.opentelemetry.sdk.metrics.common.InstrumentValueType;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.resources.Resource;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class CountAggregationTest {
  @Test
  void toMetricData() {
    Aggregation<LongAccumulation> count =
        AggregationFactory.count().create(InstrumentValueType.LONG);
    AggregatorHandle<LongAccumulation> aggregatorHandle = count.getAggregator().createHandle();
    aggregatorHandle.recordLong(10);

    MetricData metricData =
        count.toMetricData(
            Resource.getDefault(),
            InstrumentationLibraryInfo.getEmpty(),
            InstrumentDescriptor.create(
                "name",
                "description",
                "unit",
                InstrumentType.VALUE_RECORDER,
                InstrumentValueType.LONG),
            Collections.singletonMap(Labels.empty(), aggregatorHandle.accumulateThenReset()),
            0,
            100);
    assertThat(metricData).isNotNull();
    assertThat(metricData.getUnit()).isEqualTo("1");
    assertThat(metricData.getType()).isEqualTo(MetricData.Type.LONG_SUM);
  }

  @Test
  void getAggregatorFactory() {
    AggregationFactory count = AggregationFactory.count();
    assertThat(count.create(InstrumentValueType.LONG).getAggregator())
        .isInstanceOf(CountAggregator.getInstance().getClass());
    assertThat(count.create(InstrumentValueType.DOUBLE).getAggregator())
        .isInstanceOf(CountAggregator.getInstance().getClass());
  }
}
