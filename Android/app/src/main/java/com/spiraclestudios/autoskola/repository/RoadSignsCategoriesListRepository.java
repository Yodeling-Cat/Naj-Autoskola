// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.repository;

import com.spiraclestudios.autoskola.domain.RoadSignsCategoriesItem;
import java.util.ArrayList;

public class RoadSignsCategoriesListRepository {

  public ArrayList<RoadSignsCategoriesItem> getList() {
    ArrayList<RoadSignsCategoriesItem> results = new ArrayList<>();

    results.add(new RoadSignsCategoriesItem("A", "Výstražné značky", "A/a8"));
    results.add(new RoadSignsCategoriesItem("B", "Zákazové značky", "B/b31a"));
    results.add(new RoadSignsCategoriesItem("C", "Príkazové značky", "C/c4a"));
    results.add(new RoadSignsCategoriesItem("E", "Dodatkové tabuľky", "E/e9"));
    results.add(new RoadSignsCategoriesItem("II", "Informatívne iné značky", "II/ii11"));
    results.add(new RoadSignsCategoriesItem("IP", "Informatívne, prevádzkové, smerové a iné značky",
        "IP/ip10"));
    results.add(new RoadSignsCategoriesItem("IS", "Informatívne smerové značky", "IS/is5a"));
    results.add(new RoadSignsCategoriesItem("P", "Značky upravujúce prednosť v jazde", "P/p1"));
    results.add(new RoadSignsCategoriesItem("V", "Vodorovné dopravné značky", "V/v10e"));

    return results;
  }
}
