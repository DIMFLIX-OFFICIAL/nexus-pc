package com.shop.controllers;

import com.shop.database.models.Case;
import com.shop.database.models.Computer;
import com.shop.database.models.Cooler;
import com.shop.database.models.GraphicCard;
import com.shop.database.models.Motherboard;
import com.shop.database.models.PowerSupply;
import com.shop.database.models.Processor;
import com.shop.database.models.RAM;
import com.shop.database.models.User;


public class SharedData {
    public static User authenticatedUser;
    public static MainPanelController controller;
    public static Computer selectedComputer;

    public static Integer minCostFilter = 1;
    public static Integer maxCostFilter = 1000000000;
    public static Processor processorFilter;
    public static GraphicCard graphicCardFilter;
    public static Motherboard motherboardFilter;
    public static PowerSupply powerSupplyFilter;
    public static RAM ramFilter;
    public static Integer countRAMFilter = 0;
    public static Cooler coolerFilter;
    public static Case caseFilter;


    public static void setAuthenticatedUser(User value) {
        authenticatedUser = value;
    }

    public static User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public static void setMainController(MainPanelController value) {
        controller = value;
    }

    public static MainPanelController getMainController() {
        return controller;
    }

    public static void setSelectedComputer(Computer value) {
        selectedComputer = value;
    }

    public static Computer getSelectedComputer() {
        return selectedComputer;
    }

    public static Integer getMinCostFilter() {
        return minCostFilter;
    }

    public static void setMinCostFilter(Integer minCost) {
        minCostFilter = minCost;
    }

    public static Integer getMaxCostFilter() {
        return maxCostFilter;
    }

    public static void setMaxCostFilter(Integer maxCost) {
        maxCostFilter = maxCost;
    }

    public static Processor getProcessorFilter() {
        return processorFilter;
    }

    public static void setProcessorFilter(Processor processor) {
        processorFilter = processor;
    }

    public static GraphicCard getGraphicCardFilter() {
        return graphicCardFilter;
    }

    public static void setGraphicCardFilter(GraphicCard gpu) {
        graphicCardFilter = gpu;
    }

    public static Motherboard getMotherboardFilter() {
        return motherboardFilter;
    }

    public static void setMotherboardFilter(Motherboard motherboard) {
        motherboardFilter = motherboard;
    }

    public static PowerSupply getPowerSupplyFilter() {
        return powerSupplyFilter;
    }

    public static void setPowerSupplyFilter(PowerSupply ps) {
        powerSupplyFilter = ps;
    }

    public static RAM getRamFilter() {
        return ramFilter;
    }

    public static void setRamFilter(RAM ram) {
        ramFilter = ram;
    }

    public static Integer getCountRAMFilter() {
        return countRAMFilter;
    }

    public static void setCountRAMFilter(Integer countRAM) {
        countRAMFilter = countRAM;
    }

    public static Cooler getCoolerFilter() {
        return coolerFilter;
    }

    public static void setCoolerFilter(Cooler cooler) {
        coolerFilter = cooler;
    }

    public static Case getCaseFilter() {
        return caseFilter;
    }

    public static void setCaseFilter(Case value) {
        caseFilter = value;
    }
}