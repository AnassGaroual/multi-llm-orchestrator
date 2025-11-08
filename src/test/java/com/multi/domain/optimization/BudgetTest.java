/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.optimization;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Budget Value Objects")
class BudgetTest {

  @Test
  void token_budget_should_track_usage() {
    var budget = new TokenBudget(1000, 0);

    assertThat(budget.remaining()).isEqualTo(1000);
    assertThat(budget.canAfford(500)).isTrue();
    assertThat(budget.canAfford(1500)).isFalse();
  }

  @Test
  void token_budget_should_subtract_usage() {
    var budget = new TokenBudget(1000, 300);

    var updated = budget.consume(200);

    assertThat(updated.usedTokens()).isEqualTo(500);
    assertThat(updated.remaining()).isEqualTo(500);
  }

  @Test
  void token_budget_should_detect_exhaustion() {
    var budget = new TokenBudget(1000, 1000);

    assertThat(budget.isExhausted()).isTrue();
    assertThat(budget.canAfford(1)).isFalse();
  }

  @Test
  void cost_budget_should_track_spending() {
    var budget = new CostBudget(10.0, 3.5);

    assertThat(budget.remaining()).isCloseTo(6.5, within(0.01));
    assertThat(budget.canAfford(5.0)).isTrue();
    assertThat(budget.canAfford(7.0)).isFalse();
  }

  @Test
  void cost_budget_should_subtract_cost() {
    var budget = new CostBudget(10.0, 3.0);

    var updated = budget.spend(2.5);

    assertThat(updated.usedCost()).isCloseTo(5.5, within(0.01));
    assertThat(updated.remaining()).isCloseTo(4.5, within(0.01));
  }

  @Test
  void budget_should_combine_constraints() {
    var tokenBudget = new TokenBudget(1000, 500);
    var costBudget = new CostBudget(10.0, 3.0);

    var budget = new Budget(tokenBudget, costBudget);

    assertThat(budget.tokenBudget().remaining()).isEqualTo(500);
    assertThat(budget.costBudget().remaining()).isCloseTo(7.0, within(0.01));
  }

  @Test
  void budget_should_detect_any_exhaustion() {
    var tokenExhausted = new TokenBudget(1000, 1000);
    var costOk = new CostBudget(10.0, 5.0);

    var budget = new Budget(tokenExhausted, costOk);

    assertThat(budget.isExhausted()).isTrue();
  }
}
