package calories

import diode.{Dispatcher, ModelR}
import org.scalajs.dom
import scala.util.Try
import scalatags.JsDom.all._
import org.scalajs.jquery.{jQuery => $}

/**
  * Created by msciab on 16/12/15.
  */
class MealView(meals: ModelR[Array[Meal]], dispatch: Dispatcher) {

  val date = new scala.scalajs.js.Date()
  val today = "%04d-%02d-%02d".format(date.getFullYear(), date.getMonth() + 1, date.getDate())

  def render = div(cls := "row",
    div(cls := "col-md-3", caloriesForm),
    div(cls := "col-md-6", caloriesTable, logout),
    div(cls := "col-md-3", caloriesFilter))


  def logout = button("Logout", `type` := "button",
    cls := "btn btn-lg btn-primary btn-block",
    onclick := { () => dispatch(Logout(0)) }
  )

  def caloriesForm = form(role := "form",
    h2("Add a Meal"),
    div(cls := "form-group",
      label(`for` := "meal", "Meal"),
      input(id := "meal", tpe := "text",
        value := "",
        cls := "form-control")
    ),
    div(cls := "form-group",
      label(`for` := "calories", "Calories"),
      input(id := "calories", tpe := "number", min := "0",
        value := "",
        cls := "form-control")
    ),
    div(cls := "form-group",
      label(`for` := "time", "Time"),
      div(cls := "input-group bootstrap-timepicker timepicker",
        input(id := "time", tpe := "text", cls := "form-control input-small"),
        span(cls := "input-group-addon",
          i(cls := "glyphicon glyphicon-time")))
    ),
    div(cls := "form-group",
      label(`for` := "date", "Date"),
      div(cls := "input-group",
        input(id := "date", tpe := "text",
          value := today,
          cls := "form-control input-small"),
        span(cls := "input-group-addon",
          i(cls := "glyphicon glyphicon-calendar")))
    ),
    button("Add", `type` := "button",
      cls := "btn btn-lg btn-primary btn-block",
      onclick := { () =>

        val date = $("#date").value.toString
        val time = $("#time").value.toString
        val meal = $("#meal").value.toString
        val calories = Try(Integer.parseInt($("#calories").value.toString))
        if (meal.trim.size == 0) {
          dom.alert("Enter a meal.")
        } else if (calories.isFailure || calories.get < 1 || calories.get > 10000) {
          dom.alert("Enter a meaningful amount of calories.")
        } else if (time.trim.size == 0) {
          dom.alert("Enter time.")
        } else if (date.trim.size == 0) {
          dom.alert("Enter date.")
        } else {
          dispatch(Meal(date, time, meal, calories.get))
        }
      }
    ),
    script(tpe := "text/javascript")(raw(
      """$(function() {
            $("#date").datepicker({
              format: 'yyyy-mm-dd',
              autoclose: true,
              immediateUpdates: true,
              showToday: true
            });
            $("#time").timepicker({
                       showMeridian: false,
                       showSeconds: false,
                       minuteStep: 1
            });
           });
      """))
  )

  def caloriesTable = div(cls := "panel panel-default",
    table(cls := "table table-bordered",
      thead(tr(
        th("Date/Time"),
        th("Meal"),
        th("Calories")
      )), tbody(
        for (meal <- meals.value)
          yield tr(cls := s"${if(meal.isOver) "danger" else "success"}",
            td(width := "20%", i(s"${meal.date} ${meal.time}")),
            td(width := "40%", span(s"${meal.meal}")),
            td(width := "15%", b(s"${meal.calories}")),
            td(width := "15%",
              input(tpe := "button", value := "Delete",
                onclick := { () => dispatch(MealDelete(meal.id)) }))))))

  def caloriesFilter = form(role := "form",
    h4("Date Range"),
    div(cls := "form-group",
      input(id := "datefilter1", tpe := "text", value := today,
        cls := "form-control", placeholder := "Start date")),
    div(cls := "form-group",
      input(id := "datefilter2", tpe := "text",
        cls := "form-control", placeholder := "End date")),
    h4("Time Range"),
    div(cls := "form-group bootstrap-timepicker timepicker",
      input(id := "timefilter1", tpe := "text", value := "00:00",
        cls := "form-control", placeholder := "Start time")),
    div(cls := "form-group bootstrap-timepicker timepicker",
      input(id := "timefilter2", tpe := "text", value := "23:59",
        cls := "form-control", placeholder := "End time")),
    button("Filter", `type` := "button",
      cls := "btn btn-lg btn-primary btn-block",
      onclick := { () => dispatch(MealFilter) }
    ),
    script(tpe := "text/javascript")(raw(
      """$(function() {
            $("#datefilter1").datepicker({ format: 'yyyy-mm-dd' });
            $("#datefilter2").datepicker({ format: 'yyyy-mm-dd' });
            $("#timefilter1").timepicker({
              defaultTime: false,
              showMeridian: false,
              minuteStep: 1
            });
            $("#timefilter2").timepicker({
              defaultTime: false,
              showMeridian: false,
              minuteStep: 1
            });
           });
      """))
  )
}

