package com.backwards.com.backwards.watchlist.routes

import io.circe.syntax._
import com.backwards.watchlist.adt.Watchlist
import com.backwards.watchlist.adt.Watchlist.ContentId
import com.github.agourlay.cornichon.CornichonFeature
import com.github.agourlay.cornichon.core.FeatureDef

class WatchlistFeatureSpec extends CornichonFeature {
  def feature: FeatureDef = Feature(name = "Watchlist API") {
    Scenario(name = "Add an item to a customer's watchlist") {
      When I post("http://localhost:8080/watchlist/321").withBody(Watchlist.Item(ContentId("12345").right.get).asJson)
      Then assert status.is(expected = 201)

      Then I get("http://localhost:8080/watchlist/321")
      And assert status.is(expected = 200)

      And assert body.is("""
      {
        "customerId": "321",
        "items": [{
          "contentId": "12345"
        }]
      }""")
    }
  }
}