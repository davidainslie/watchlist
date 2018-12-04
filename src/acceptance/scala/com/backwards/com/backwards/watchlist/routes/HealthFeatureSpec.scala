package com.backwards.com.backwards.watchlist.routes

import com.github.agourlay.cornichon.CornichonFeature
import com.github.agourlay.cornichon.core.FeatureDef

class HealthFeatureSpec extends CornichonFeature {
  def feature: FeatureDef = Feature(name = "Watchlist API") {
    Scenario(name = "Up and running") {
      When I get("http://localhost:8080/healthz")
      Then assert status.is(expected = 200)
    }
  }
}