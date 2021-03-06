/*
 * @(#)SearchCommand.java 1.0 11.12.2016
 */

package ru.solpro.view;

import ru.solpro.controller.SystemException;
import ru.solpro.controller.TrainModelController;
import ru.solpro.controller.RouteModelController;
import ru.solpro.controller.StationModelController;
import ru.solpro.model.Train;
import ru.solpro.model.Route;
import ru.solpro.model.Station;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Команда поиска.
 * @version 1.0 11 декабря 2016
 * @author Protsvetov Danila
 */
public class SearchCommand implements Command {

    /**
     * Выполнение команды.
     * @param args    аргументы
     * @return true - продолжить выполнение, false - завершить выполнение.
     * @throws SystemException  ошибка при работе пользователя с программой.
     * @throws IOException  ошибка ввыода/вывода
     */
    @Override
    public boolean execute(String[] args) throws SystemException, IOException {
        if (args == null || args.length < 1 || args.length > 1) {
            printHelp();
            return true;
        }
        switch (args[0].toUpperCase()) {
            case "STATION":
                searchStation();
                break;
            case "ROUTE":
                searchRoute();
                break;
            case "TRAIN":
                searchTrain();
                break;
            default:
                printHelp();
        }
        return true;
    }

    /**
     * Распечатать справку по команде.
     */
    @Override
    public void printHelp() {
        System.out.println("Поддерживаются символы * и ?");
        System.out.println("Список параметров команды:");
        System.out.println("STATION - поиск станции.");
        System.out.println("ROUTE - поиск маршрута.");
        System.out.println("TRAIN - поиск поезда по его номеру.");
    }

    /**
     * Имя команды.
     * @return имя команды.
     */
    @Override
    public String getName() {
        return "SEARCH";
    }

    /**
     * Описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription() {
        return "Поиск данных в системе.";
    }

	/**
	 * Поиск станции.
	 */
    private void searchStation() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StationModelController stationController = StationModelController.getInstance();

        System.out.print("\tВведите строку для поиска: ");
        String string = reader.readLine();
        ArrayList<Station> result = stationController.search(string);
        System.out.println("\tРезультат поиска:");
        if (result.isEmpty()) {
            System.out.println("\tНичего не найдено.");
            return;
        }
        for (Station station : result) {
            System.out.println("\t[" + station.getId() + "] " + station);
        }
    }

	/**
	 * Поиск маршрута.
	 */
    private void searchRoute() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        RouteModelController routeController = RouteModelController.getInstance();

        System.out.print("\tВведите строку для поиска: ");
        String string = reader.readLine();
        ArrayList<Route> result = routeController.search(string);
        System.out.println("\tРезультат поиска:");
        if (result.isEmpty()) {
            System.out.println("\tНичего не найдено.");
            return;
        }
        for (Route route : result) {
            System.out.println("\t[" + route.getId() + "] " + route);
        }
    }

	/**
	 * Поиск поезда.
	 */
    private void searchTrain() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        TrainModelController trainModelController = TrainModelController.getInstance();

        System.out.print("\tВведите номер поезда: ");
        Integer integer = Integer.parseInt(reader.readLine());
        Train result = trainModelController.search(integer);
        if (result == null) {
            System.out.println("\tНичего не найдено.");
            return;
        }
        System.out.println("\t" + result);
    }
}
