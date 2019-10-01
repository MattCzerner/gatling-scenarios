import io.gatling.core.Predef._ 
import io.gatling.http.Predef._
import scala.concurrent.duration._

object Walk {
  val headers_classic = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Upgrade-Insecure-Requests" -> "1"
  )

  val headers_ajax = Map("X-Requested-With" -> "XMLHttpRequest")

  val headers_ajax_html = Map(
    "Accept" -> "text/html, */*; q=0.01",
    "X-Requested-With" -> "XMLHttpRequest"
  )

  val headers_ajax_submit = Map(
    "Content-Type" -> "application/x-www-form-urlencoded; charset=UTF-8",
    "X-Requested-With" -> "XMLHttpRequest"
  )

  val headers_ajax_submit_html = Map(
    "Accept" -> "text/html, */*; q=0.01",
    "Content-Type" -> "application/x-www-form-urlencoded; charset=UTF-8",
    "X-Requested-With" -> "XMLHttpRequest"
  )

  val homepage = http("Homepage")
    .get("/")
    .headers(headers_classic)

  val categoryPanel = http("Category panel")
    .get("/categoryPanel/2/")
    .headers(headers_ajax_html)

  val categoryList = http("Category list")
    .get("/electronics/")
    .headers(headers_classic)

  val categoryListFilterPrice = http("Category list filtering")
    .get("/electronics/?product_filter_form[minimalPrice]=0&product_filter_form[maximalPrice]=20000")
    .headers(headers_ajax)

  val categoryListFilterPriceBrand = http("Category list filtering")
    .get("/electronics/?product_filter_form[minimalPrice]=0&product_filter_form[maximalPrice]=20000&product_filter_form[brands][]=4")
    .headers(headers_ajax)

  def cartAdd(productId: Int) = http("Add product to cart")
    .post("/cart/addProductAjax/")
    .headers(headers_ajax_submit_html)
    .formParam("add_product_form[quantity]", "1")
    .formParam("add_product_form[productId]", productId)
    .resources(
      http("Update cart box")
      .get("/cart/box/")
      .headers(headers_ajax)
    )

  val productDetail = http("Product detail")
    .get("/22-sencor-sle-22f46dm4-hello-kitty/")
    .headers(headers_classic)

  val cart = http("Cart")
    .get("/cart/")
    .headers(headers_classic)

  val search = http("Search")
    .get("/search?q=canon")
    .headers(headers_classic)

  val searchOption = System.getProperty("search")

  var walk = {
    if (searchOption == null) {
      exec(homepage)
	.pause(100.milliseconds)
	.exec(categoryPanel)
	.pause(100.milliseconds)
	.exec(categoryList)
	.pause(100.milliseconds)
	.exec(categoryListFilterPrice)
	.pause(100.milliseconds)
	.exec(cartAdd(1))
	.pause(100.milliseconds)
	.exec(productDetail)
	.pause(100.milliseconds)
	.exec(cart)
	.pause(100.milliseconds)
    } else {
      exec(homepage)
	.pause(100.milliseconds)
	.exec(categoryPanel)
	.pause(100.milliseconds)
	.exec(search)
	.pause(100.milliseconds)
	.exec(categoryList)
	.pause(100.milliseconds)
	.exec(categoryListFilterPrice)
	.pause(100.milliseconds)
	.exec(cartAdd(1))
	.pause(100.milliseconds)
	.exec(productDetail)
	.pause(100.milliseconds)
	.exec(cart)
	.pause(100.milliseconds)
    }
  }
}

object WalkRepeat {
  val times = System.getProperty("repeat")

  val walk = repeat(times.toInt) {
    Walk.walk
  }
}

class PerformatorLvl1Simulation extends Simulation { 
  val baseUrl = System.getProperty("baseUrl")

  val httpConf = http 
    .baseURL(baseUrl) 
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // 6
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("Walk")
    .exec(WalkRepeat.walk)

  val users = System.getProperty("users")

  setUp(
    scn.inject(atOnceUsers(users.toInt))
  ).protocols(httpConf)
}

