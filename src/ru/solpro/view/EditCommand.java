package ru.solpro.view;

import ru.solpro.controller.ElectricTrainModelController;
import ru.solpro.controller.RouteModelController;
import ru.solpro.controller.StationModelController;
import ru.solpro.controller.SystemException;
import ru.solpro.model.ElectricTrain;
import ru.solpro.model.Route;
import ru.solpro.model.Schedule;
import ru.solpro.model.Station;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by danila on 06.12.2016.
 * @author Protsvetov Danila
 */
public class EditCommand extends AlwaysCommand implements Command {
    @Override
    public boolean execute(String[] args) throws SystemException, IOException {
        if (args == null || args.length < 1 || args.length > 1) {
            System.out.println("Неверный аргумент у команды.");
            printHelp();
            return true;
        }
        switch (args[0].toUpperCase()) {
            case "STATION":
                editStation();
                break;
            case "ROUTE":
                editRoute();
                break;
            case "TRAIN":
                editTrain();
                break;
            default:
                printHelp();
        }
        return true;
    }

    @Override
    public void printHelp() {
        System.out.println("Данная команда позволяет изменять данные в системе.");
        System.out.println("Список параметров команды:");
        System.out.println("STATION - изменение станции.");
        System.out.println("ROUTE - изменение маршрута.");
        System.out.println("TRAIN - изменение поезда.");
    }

    @Override
    public String getName() {
        return "EDIT";
    }

    @Override
    public String getDescription() {
        return "Редактирование данных.";
    }

    private void editTrain() throws IOException, SystemException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        RouteModelController routeModelController = RouteModelController.getInstance();
        ElectricTrainModelController electricTrainModelController = ElectricTrainModelController.getInstance();

        System.out.print("\tВведите номер поезда для редактирования: ");
        Integer numberTrain = Integer.parseInt(reader.readLine());
        ElectricTrain editElectricTrain = electricTrainModelController.search(numberTrain);
        if (editElectricTrain == null) {
            error("Не найден поезд для редактирования.");
        }
        System.out.println("\tВыбран поезд: " + editElectricTrain);

        System.out.print("\tВведите новый номер поезда (если изменять не нужно, оставте поле пустым): ");
        String str1 = reader.readLine();
        Integer newNumberTrain;
        if (str1.equals("")) {
            newNumberTrain = editElectricTrain.getTrainNumber();
        } else {
            newNumberTrain = Integer.parseInt(str1);
        }

        System.out.print("\tВведите новый id маршрута (если изменять не нужно, оставте поле пустым): ");
        String str2 = reader.readLine();
        Integer newRouteId;
        if (str2.equals("")) {
            newRouteId = editElectricTrain.getTrainTimetable().last().getRoute().getId();
        } else {
            newRouteId = Integer.parseInt(str2);
            Route route = routeModelController.search(newRouteId);
            if (route == null) {
                error("Маршрут не найден.");
            }
            editElectricTrain.clearTrainTimetable();
            editTrainRoute(editElectricTrain, route);
        }

        for (Schedule schedule : editElectricTrain.getTrainTimetable()) {
            schedule.setRoute(routeModelController.search(newRouteId));
        }
        editElectricTrain.setTrainNumber(newNumberTrain);
    }

    /**
     * Меняет маршрут и добавляет первую дату в расписание.
     * @throws IOException
     * @throws SystemException
     */
    private void editTrainRoute(ElectricTrain electricTrain, Route routeId) throws IOException, SystemException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ElectricTrainModelController electricTrainController = ElectricTrainModelController.getInstance();

        System.out.println("\tПосле изменения маршрута необходимо добавить запись в расписание.");
        System.out.print("\tДата отправления (dd.mm.yyyy): ");
        String strDateDep = reader.readLine();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate dateDep = LocalDate.parse(strDateDep, dateFormatter);

        System.out.print("\tВремя отправления (hh:mm): ");
        String strTimeDep = reader.readLine();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime timeDep = LocalTime.parse(strTimeDep, timeFormatter);
        LocalDateTime depDateTime = LocalDateTime.of(dateDep, timeDep);

        System.out.print("\tВремя движения до конечного пункта (часов): ");
        String strTimeArrHours = reader.readLine();
        Integer timeArrHours = Integer.parseInt(strTimeArrHours);

        System.out.print("\tВремя движения до конечного пункта (минут): ");
        String strTimeArrMinutes = reader.readLine();
        if (strTimeArrMinutes.equals("")) {
            strTimeArrMinutes = "0";
        }
        Integer timeArrMinutes = Integer.parseInt(strTimeArrMinutes);

        if (timeArrMinutes == 0) {
            electricTrainController.addScheduleLine(routeId.getId(), electricTrain.getTrainNumber(), depDateTime, timeArrHours);
        } else {
            electricTrainController.addScheduleLine(routeId.getId(), electricTrain.getTrainNumber(), depDateTime, timeArrHours, timeArrMinutes);
        }
        System.out.println("Расписание успешно добавлено.");
    }

    private void editRoute() throws IOException, SystemException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StationModelController stationModelController = StationModelController.getInstance();
        RouteModelController routeModelController = RouteModelController.getInstance();

        System.out.print("\tВведите номер маршрута для редактирования: ");
        Integer number = Integer.parseInt(reader.readLine());
        Route editRoute = routeModelController.search(number);
        if (editRoute == null) {
            error("Не найден маршрут для редактирования.");
        }
        System.out.println("\tВыбран маршрут: " + editRoute);

        System.out.print("\tВведите новый id станции отправления (если изменять не нужно, оставте поле пустым): ");
        String str1 = reader.readLine();
        Integer newIdDepSt;
        if (str1.equals("")) {
            newIdDepSt = editRoute.getDeparture().getId();
        } else {
            newIdDepSt = Integer.parseInt(str1);
        }
        Station newDepSt = stationModelController.search(newIdDepSt);
        if (newDepSt == null) {
            error("Станция с id=" + newIdDepSt + " не найдена.");
        }

        System.out.print("\tВведите новый id станции назначения (если изменять не нужно, оставте поле пустым): ");
        String str2 = reader.readLine();
        Integer newIdArrSt;
        if (str2.equals("")) {
            newIdArrSt = editRoute.getArrival().getId();
        } else {
            newIdArrSt = Integer.parseInt(str2);
        }
        Station newArrSt = stationModelController.search(newIdArrSt);
        if (newArrSt == null) {
            error("Станция с id=" + newIdArrSt + " не найдена.");
        }

        editRoute.setDeparture(newDepSt);
        editRoute.setArrival(newArrSt);
    }

    private void editStation() throws IOException, SystemException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StationModelController stationModelController = StationModelController.getInstance();

        System.out.print("\tВведите номер станции для редактирования: ");
        Integer number = Integer.parseInt(reader.readLine());
        Station editStation = stationModelController.search(number);
        if (editStation == null) {
            error("Не найдена станция для редактирования.");
        }
        System.out.print("\tВведите новое название станции: ");
        String newNameStation = reader.readLine();
        if (!stationModelController.search(newNameStation).isEmpty()) {
            error("Такое название уже существует.");
        }
        editStation.setNameStation(newNameStation);
        System.out.println("\tРеадктирование завершено.");
    }
}
